package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;
import org.molgenis.vipannotate.util.*;

@RequiredArgsConstructor
public class AnnotationDbBuilder {
  private final InputAnalyzerFactory inputAnalyzerFactory;
  private final AnnotationSpecResolver annotationSpecResolver;
  private final AnnotatedFeatureReaderFactory annotationReaderFactory;
  private final AnnotationDatasetEncoderFactory annotationDatasetEncoderFactory;
  private final ResolvedAnnotationDbSpecWriter specWriter;

  public void buildDb(
      Input input, AnnotationDbSpec annotationDbSpec, BinaryPartitionWriter partitionWriter) {
    InputFormat inputFormat = annotationDbSpec.inputFormat();
    AnnotationSchema annotationSchema = annotationDbSpec.annotationSchema();
    AnnotationSpecs annotationSpecs = annotationSchema.annotationSpecs();

    // pass #1 collect stats from input data
    InputAnalyses inputAnalyses;
    try (InputAnalyzer inputAnalyzer = inputAnalyzerFactory.create(input, inputFormat)) {
      Logger.debug("analyzing input ...");
      long startAnalyzeInput = System.currentTimeMillis();
      inputAnalyses = inputAnalyzer.analyze(annotationSpecs);
      Logger.debug("analyzing input done in %sms", System.currentTimeMillis() - startAnalyzeInput);
    }

    // create resolved specs
    ResolvedAnnotationSpecs resolvedAnnotationSpecs = annotationSpecResolver.resolve(inputAnalyses);

    ResolvedAnnotationDbSpec resolvedAnnotationDbSpec =
        new ResolvedAnnotationDbSpec(
            annotationDbSpec.specVersion(),
            annotationDbSpec.specId(),
            annotationDbSpec.specDescription(),
            new ResolvedAnnotationSchema(
                annotationSchema.annotationType(),
                inputAnalyses.sequenceVariantTypes(),
                resolvedAnnotationSpecs,
                annotationSchema.annotationSelector()));
    // TODO improve spec logging
    Logger.debug("resolved annotation specification '%s'", resolvedAnnotationDbSpec);
    specWriter.write(resolvedAnnotationDbSpec, partitionWriter);

    // pass #2 build annotation database using final annotation specs
    try (AnnotatedFeatureReader annotationReader =
        annotationReaderFactory.create(input, inputFormat, resolvedAnnotationSpecs)) {

      switch (inputFormat.annotationType()) {
        case INTERVAL, POSITION ->
            // FIXME get rid of cast
            createAnnotatedIntervalDb(
                (Iterator) annotationReader, resolvedAnnotationSpecs, partitionWriter);
        case SEQUENCE_VARIANT ->
            // FIXME get rid of cast
            createCompositeAnnotatedSequenceVariantDb(
                (Iterator) annotationReader, resolvedAnnotationSpecs, partitionWriter);
      }
    }
  }

  private void createCompositeAnnotatedSequenceVariantDb(
      Iterator<AnnotatedSequenceVariant<CompositeAnnotation>> annotatedIterator,
      ResolvedAnnotationSpecs annotationSpecs,
      BinaryPartitionWriter partitionWriter) {
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

  private void createAnnotatedIntervalDb(
      Iterator<AnnotatedPosition<CompositeAnnotation>> annotatedPosIterator,
      ResolvedAnnotationSpecs annotationSpecs,
      BinaryPartitionWriter partitionWriter) {
    List<
            AnnotatedIntervalPartitionWriter<
                Position, CompositeAnnotation, AnnotatedPosition<CompositeAnnotation>>>
        partitionWriters = new ArrayList<>(annotationSpecs.size());

    AtomicInteger atomicInteger = new AtomicInteger();
    annotationSpecs.forEach(
        (annotationDatasetId, annotationSpec) -> {
          IndexedAnnotationEncoder<Annotation> annotationEncoder =
              createIndexedEncoder(annotationSpec);

          int annotationIndex = atomicInteger.getAndIncrement();
          partitionWriters.add(
              new AnnotatedPositionPartitionWriter<>(
                  annotationDatasetId,
                  new IndexedAnnotatedFeatureDatasetEncoder<>(annotationEncoder),
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

  private static IndexedAnnotationEncoder<Annotation> createIndexedEncoder(
      ResolvedAnnotationSpec annotationSpec) {
    // FIXME do not create factories that are already created by AnnotationDatasetEncoderFactory
    AnnotationEncoder<Annotation> annotationEncoder =
        (AnnotationEncoder<Annotation>)
            switch (annotationSpec) {
              case ResolvedEnumAnnotationSpec _ ->
                  throw new UnsupportedOperationException(); // FIXME
              case ResolvedEnumSetAnnotationSpec _ ->
                  throw new UnsupportedOperationException(); // FIXME
              case ResolvedFloatAnnotationSpec spec ->
                  new FloatAnnotationEncoderFactory(new ValueWriterFactory()).createIndexed(spec);
              case ResolvedIntAnnotationSpec spec ->
                  new IntAnnotationEncoderFactory(new ValueWriterFactory()).createIndexed(spec);
            };
    return new IndexedAnnotationEncoder<>(annotationEncoder);
  }

  public static AnnotationDbBuilder create() {
    AnnotationSpecResolver annotationSpecResolver = new AnnotationSpecResolver();
    InputAnalyzerFactory inputAnalyzerFactory = InputAnalyzerFactory.create();
    AnnotatedFeatureReaderFactory annotationReaderFactory = AnnotatedFeatureReaderFactory.create();
    AnnotationDatasetEncoderFactory annotationDatasetEncoderFactory =
        AnnotationDatasetEncoderFactory.create();
    ResolvedAnnotationDbSpecWriter specWriter = ResolvedAnnotationDbSpecWriter.create();
    return new AnnotationDbBuilder(
        inputAnalyzerFactory,
        annotationSpecResolver,
        annotationReaderFactory,
        annotationDatasetEncoderFactory,
        specWriter);
  }
}
