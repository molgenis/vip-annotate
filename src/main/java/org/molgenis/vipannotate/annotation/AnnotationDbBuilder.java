package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;
import org.molgenis.vipannotate.util.*;

@RequiredArgsConstructor
public class AnnotationDbBuilder {
  private final InputAnalyzerFactory inputAnalyzerFactory;
  private final AnnotationDbSpecResolver dbSpecResolver;
  private final AnnotatedFeatureReaderFactory annotationReaderFactory;
  private final AnnotationDatasetEncoderFactory annotationDatasetEncoderFactory;
  private final ResolvedAnnotationDbSpecWriter specWriter;

  public void buildDb(Path input, AnnotationDbSpec dbSpec, BinaryPartitionWriter partitionWriter) {
    InputFormat inputFormat = dbSpec.inputFormat();

    // pass #1 collect stats from input data
    InputAnalyses inputAnalyses;
    try (InputAnalyzer inputAnalyzer = inputAnalyzerFactory.create(input, inputFormat)) {
      Logger.debug("analyzing input ...");
      long startAnalyzeInput = System.currentTimeMillis();
      inputAnalyses = inputAnalyzer.analyze(dbSpec.annotationSchema().annotationSpecs());
      Logger.debug("analyzing input done in %sms", System.currentTimeMillis() - startAnalyzeInput);
    }

    // resolve and persist db specs
    ResolvedAnnotationDbSpec resolvedDbSpec = dbSpecResolver.resolve(dbSpec, inputAnalyses);
    Logger.debug("resolved annotation specification\n%s", formatAnnotationSpecs(resolvedDbSpec));
    specWriter.write(resolvedDbSpec, partitionWriter);

    // pass #2 build annotation database using resolved specs
    ResolvedAnnotationSchema resolvedSchema = resolvedDbSpec.annotationSchema();
    ResolvedAnnotationSpecs resolvedSpecs = resolvedSchema.annotationSpecs();
    try (AnnotatedFeatureReader annotationReader =
        annotationReaderFactory.create(input, inputFormat, resolvedSpecs)) {

      switch (resolvedSchema.annotationType()) {
        case INTERVAL -> throw new UnsupportedOperationException(); // FIXME implement
        case POSITION ->
            createAnnotatedPositionDb(annotationReader, resolvedSpecs, partitionWriter);
        case SEQUENCE_VARIANT ->
            createCompositeAnnotatedSequenceVariantDb(
                annotationReader, resolvedSpecs, partitionWriter);
      }
    }
  }

  private static String formatAnnotationSpecs(ResolvedAnnotationDbSpec spec) {
    StringBuilder builder = new StringBuilder();
    builder.append("  %-14s:  %s (%s)\n".formatted("name", spec.specId(), spec.specVersion()));
    if (spec.specDescription() != null) {
      builder.append("  %-14s:  %s\n".formatted("description", spec.specDescription()));
    }
    ResolvedAnnotationSchema annotationSchema = spec.annotationSchema();
    builder.append(
        "  %-14s:  type=%s\n".formatted("annotations", annotationSchema.annotationType()));
    annotationSchema
        .annotationSpecs()
        .annotationSpecMap()
        .forEach((key, value) -> formatAnnotationSpec(builder, key, value));

    return builder.deleteCharAt(builder.length() - 1).toString();
  }

  private static void formatAnnotationSpec(
      StringBuilder stringBuilder, String name, ResolvedAnnotationSpec spec) {
    stringBuilder.append("    %-12s:  ".formatted(name));
    switch (spec) {
      case ResolvedEnumAnnotationSpec _ ->
          stringBuilder.append("type=%-16s  storage_type=%-12s".formatted("enum", "bit-packing"));
      case ResolvedEnumSetAnnotationSpec _ ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s".formatted("enum_set", "bit-packing"));
      case ResolvedFloatAnnotationSpec floatSpec ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s"
                  .formatted("floating_point", floatSpec.storageType()));
      case ResolvedIntAnnotationSpec intSpec ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s".formatted("integer", intSpec.storageType()));
    }

    if (spec.description() != null) {
      stringBuilder.append("  description=%s".formatted(spec.description()));
    }
    stringBuilder.append('\n');
  }

  private void createCompositeAnnotatedSequenceVariantDb(
      AnnotatedFeatureReader annotationReader,
      ResolvedAnnotationSpecs annotationSpecs,
      BinaryPartitionWriter partitionWriter) {
    // FIXME get rid of cast
    Iterator<AnnotatedSequenceVariant<CompositeAnnotation>> annotatedIterator =
        (Iterator) annotationReader;
    List<
            AnnotatedIntervalPartitionWriter<
                SequenceVariant,
                CompositeAnnotation,
                AnnotatedSequenceVariant<CompositeAnnotation>>>
        partitionWriters = new ArrayList<>(annotationSpecs.size());

    AtomicInteger atomicInteger = new AtomicInteger();
    annotationSpecs.forEach(
        (annotationId, annotationSpec) -> {
          AnnotationDatasetEncoder<Annotation> annotationDatasetEncoder =
              annotationDatasetEncoderFactory.createAnnotationDatasetEncoder(annotationSpec);

          int annotationIndex = atomicInteger.getAndIncrement();
          partitionWriters.add(
              new AnnotatedSequenceVariantPartitionWriter<>(
                  annotationId,
                  annotationDatasetEncoder,
                  partitionWriter,
                  variant -> variant.getAnnotation().annotations()[annotationIndex]));
        });

    // TODO check if only needs to be created once
    VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
    MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexDispatcherWriter =
        SequenceVariantAnnotationIndexDispatcherWriterFactory.create(memBufferFactory)
            .createWriter();

    try (CompositeAnnotatedIntervalPartitionWriter<
            SequenceVariant, AnnotatedSequenceVariant<CompositeAnnotation>>
        variantPartitionWriter =
            new CompositeAnnotatedIntervalPartitionWriter<>(partitionWriters)) {

      new AnnotatedSequenceVariantDbWriter<>(
              variantPartitionWriter,
              new SequenceVariantAnnotationIndexWriter<>(indexDispatcherWriter, partitionWriter),
              SequenceVariantEncoderDispatcherFactory.create())
          .write(annotatedIterator);
    }
  }

  private void createAnnotatedPositionDb(
      AnnotatedFeatureReader annotationReader,
      ResolvedAnnotationSpecs annotationSpecs,
      BinaryPartitionWriter partitionWriter) {
    // fill all positions without annotations with null annotations
    Annotation nullAnnotation = createNullAnnotation(annotationSpecs);
    DensePositionAnnotatedFeatureReader denseAnnotationReader =
        new DensePositionAnnotatedFeatureReader(annotationReader, nullAnnotation);
    // FIXME get rid of cast
    Iterator<AnnotatedPosition<CompositeAnnotation>> annotatedPosIterator =
        (Iterator) denseAnnotationReader;
    List<
            AnnotatedIntervalPartitionWriter<
                Position, CompositeAnnotation, AnnotatedPosition<CompositeAnnotation>>>
        partitionWriters = new ArrayList<>(annotationSpecs.size());

    AtomicInteger atomicInteger = new AtomicInteger();
    annotationSpecs.forEach(
        (annotationDatasetId, annotationSpec) -> {
          AnnotationDatasetEncoder<Annotation> annotationDatasetEncoder =
              annotationDatasetEncoderFactory.createAnnotationDatasetEncoder(annotationSpec);

          int annotationIndex = atomicInteger.getAndIncrement();
          partitionWriters.add(
              new AnnotatedPositionPartitionWriter<>(
                  annotationDatasetId,
                  annotationDatasetEncoder,
                  partitionWriter,
                  variant -> variant.getAnnotation().annotations()[annotationIndex]));
        });

    try (CompositeAnnotatedIntervalPartitionWriter<Position, AnnotatedPosition<CompositeAnnotation>>
        posPartitionWriter = new CompositeAnnotatedIntervalPartitionWriter<>(partitionWriters)) {
      AnnotatedIntervalDbWriter<
              Position, CompositeAnnotation, AnnotatedPosition<CompositeAnnotation>>
          annotationDbWriter = new AnnotatedIntervalDbWriter<>(posPartitionWriter);

      annotationDbWriter.write(annotatedPosIterator);
    }
  }

  public static AnnotationDbBuilder create() {
    AnnotationSpecResolver annotationSpecResolver = new AnnotationSpecResolver();
    AnnotationDbSpecResolver annotationDbSpecResolver =
        new AnnotationDbSpecResolver(annotationSpecResolver);
    InputAnalyzerFactory inputAnalyzerFactory = InputAnalyzerFactory.create();
    AnnotatedFeatureReaderFactory annotationReaderFactory = AnnotatedFeatureReaderFactory.create();
    AnnotationDatasetEncoderFactory annotationDatasetEncoderFactory =
        AnnotationDatasetEncoderFactory.create();
    ResolvedAnnotationDbSpecWriter specWriter = ResolvedAnnotationDbSpecWriter.create();
    return new AnnotationDbBuilder(
        inputAnalyzerFactory,
        annotationDbSpecResolver,
        annotationReaderFactory,
        annotationDatasetEncoderFactory,
        specWriter);
  }

  private Annotation createNullAnnotation(ResolvedAnnotationSpecs specs) {
    List<Annotation> annotations = new ArrayList<>(specs.size());
    specs.forEach(
        (_, spec) ->
            annotations.add(
                switch (spec) {
                  case ResolvedEnumAnnotationSpec _ -> new StringAnnotation(null);
                  case ResolvedEnumSetAnnotationSpec _ -> new StringListAnnotation(new String[0]);
                  case ResolvedFloatAnnotationSpec _ -> new NullableFloatAnnotation();
                  case ResolvedIntAnnotationSpec _ -> new NullableIntAnnotation();
                }));
    return new CompositeAnnotation(annotations.toArray(new Annotation[0]));
  }
}
