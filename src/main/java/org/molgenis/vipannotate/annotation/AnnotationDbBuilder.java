package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.annotation.def.*;
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
  private final AnnotationDbSpecWriter specWriter;

  public void buildDb(Path input, AnnotationDbDef dbDef, BinaryPartitionWriter partitionWriter) {
    InputFormat inputFormat = dbDef.inputFormat();

    // pass #1 collect stats from input data
    InputAnalyses inputAnalyses;
    try (InputAnalyzer inputAnalyzer = inputAnalyzerFactory.create(input, inputFormat)) {
      Logger.debug("analyzing input ...");
      long startAnalyzeInput = System.currentTimeMillis();
      inputAnalyses = inputAnalyzer.analyze(dbDef.annotationsDef());
      Logger.debug("analyzing input done in %sms", System.currentTimeMillis() - startAnalyzeInput);
    }

    // resolve db definition into db specs and persist
    AnnotationDbSpec dbSpec = dbSpecResolver.resolve(dbDef, inputAnalyses);
    Logger.debug("annotation specification\n%s", formatAnnotationSpecs(dbSpec));
    specWriter.write(dbSpec, partitionWriter);

    // pass #2 build annotation database using db specs
    AnnotationsSpec annotationsSpec = dbSpec.annotationsSpec();
    try (AnnotatedFeatureReader annotationReader =
        annotationReaderFactory.create(input, inputFormat, annotationsSpec)) {

      switch (annotationsSpec.annotationType()) {
        case INTERVAL -> throw new UnsupportedOperationException(); // FIXME implement
        case POSITION ->
            createAnnotatedPositionDb(annotationReader, annotationsSpec, partitionWriter);
        case SEQUENCE_VARIANT ->
            createCompositeAnnotatedSequenceVariantDb(
                annotationReader, annotationsSpec, partitionWriter);
      }
    }
  }

  private static String formatAnnotationSpecs(AnnotationDbSpec spec) {
    StringBuilder builder = new StringBuilder();
    builder.append("  %-14s:  %s (%s)\n".formatted("name", spec.id(), spec.version()));
    if (spec.description() != null) {
      builder.append("  %-14s:  %s\n".formatted("description", spec.description()));
    }
    AnnotationsSpec annotationsSpec = spec.annotationsSpec();
    builder.append(
        "  %-14s:  type=%s\n".formatted("annotations", annotationsSpec.annotationType()));
    annotationsSpec.forEach((key, value) -> formatAnnotationSpec(builder, key, value));

    return builder.deleteCharAt(builder.length() - 1).toString();
  }

  private static void formatAnnotationSpec(
      StringBuilder stringBuilder, String name, AnnotationSpec spec) {
    stringBuilder.append("    %-12s:  ".formatted(name));
    switch (spec) {
      case EnumAnnotationSpec _ ->
          stringBuilder.append("type=%-16s  storage_type=%-12s".formatted("enum", "bit-packing"));
      case EnumSetAnnotationSpec _ ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s".formatted("enum_set", "bit-packing"));
      case FloatAnnotationSpec floatSpec ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s"
                  .formatted("floating_point", floatSpec.storageType()));
      case IntAnnotationSpec intSpec ->
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
      AnnotationsSpec annotationSpecs,
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
      AnnotationsSpec annotationsSpec,
      BinaryPartitionWriter partitionWriter) {
    // fill all positions without annotations with null annotations
    Annotation nullAnnotation = createNullAnnotation(annotationsSpec);
    DensePositionAnnotatedFeatureReader denseAnnotationReader =
        new DensePositionAnnotatedFeatureReader(annotationReader, nullAnnotation);
    // FIXME get rid of cast
    Iterator<AnnotatedPosition<CompositeAnnotation>> annotatedPosIterator =
        (Iterator) denseAnnotationReader;
    List<
            AnnotatedIntervalPartitionWriter<
                Position, CompositeAnnotation, AnnotatedPosition<CompositeAnnotation>>>
        partitionWriters = new ArrayList<>(annotationsSpec.size());

    AtomicInteger atomicInteger = new AtomicInteger();
    annotationsSpec.forEach(
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
    AnnotationsSpecResolver annotationsSpecResolver = new AnnotationsSpecResolver();
    AnnotationDbSpecResolver annotationDbSpecResolver =
        new AnnotationDbSpecResolver(annotationsSpecResolver);
    InputAnalyzerFactory inputAnalyzerFactory = InputAnalyzerFactory.create();
    AnnotatedFeatureReaderFactory annotationReaderFactory = AnnotatedFeatureReaderFactory.create();
    AnnotationDatasetEncoderFactory annotationDatasetEncoderFactory =
        AnnotationDatasetEncoderFactory.create();
    AnnotationDbSpecWriter specWriter = AnnotationDbSpecWriter.create();
    return new AnnotationDbBuilder(
        inputAnalyzerFactory,
        annotationDbSpecResolver,
        annotationReaderFactory,
        annotationDatasetEncoderFactory,
        specWriter);
  }

  private Annotation createNullAnnotation(AnnotationsSpec annotationsSpec) {
    List<Annotation> annotations = new ArrayList<>(annotationsSpec.size());
    annotationsSpec.forEach(
        (_, spec) ->
            annotations.add(
                switch (spec) {
                  case EnumAnnotationSpec _ -> new StringAnnotation(null);
                  case EnumSetAnnotationSpec _ -> new StringListAnnotation(new String[0]);
                  case FloatAnnotationSpec _ -> new NullableFloatAnnotation();
                  case IntAnnotationSpec _ -> new NullableIntAnnotation();
                }));
    return new CompositeAnnotation(annotations.toArray(new Annotation[0]));
  }
}
