package org.molgenis.vipannotate.annotation.gnomad;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotatedIntervalPartitionWriter;
import org.molgenis.vipannotate.annotation.AnnotatedSequenceVariant;
import org.molgenis.vipannotate.annotation.BinaryPartitionWriter;
import org.molgenis.vipannotate.annotation.Partition;
import org.molgenis.vipannotate.annotation.SequenceVariant;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class GnomAdAnnotatedSequenceVariantPartitionWriter
    implements AnnotatedIntervalPartitionWriter<
        SequenceVariant, GnomAdAnnotation, AnnotatedSequenceVariant<GnomAdAnnotation>> {
  private final GnomAdAnnotationDatasetEncoder gnomAdAnnotationDataSetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;

  @Override
  public void write(
      Partition<SequenceVariant, GnomAdAnnotation, AnnotatedSequenceVariant<GnomAdAnnotation>>
          partition) {
    Partition.Key partitionKey = partition.getKey();
    List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedFeatures =
        partition.getAnnotatedIntervals();

    writeSource(partitionKey, annotatedFeatures);
    writeAf(partitionKey, annotatedFeatures);
    writeFaf95(partitionKey, annotatedFeatures);
    writeFaf99(partitionKey, annotatedFeatures);
    writeHn(partitionKey, annotatedFeatures);
    writeFilters(partitionKey, annotatedFeatures);
    writeCov(partitionKey, annotatedFeatures);
  }

  private void writeSource(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeSources(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().source()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "src", memoryBuffer);
  }

  private void writeAf(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeAf(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().af()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "af", memoryBuffer);
  }

  private void writeFaf95(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeFaf95(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().faf95()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "faf95", memoryBuffer);
  }

  private void writeFaf99(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeFaf99(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().faf99()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "faf99", memoryBuffer);
  }

  private void writeHn(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeHn(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().hn()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "hn", memoryBuffer);
  }

  private void writeFilters(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeFilters(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().filters()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "filters", memoryBuffer);
  }

  private void writeCov(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeCov(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().cov()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "cov", memoryBuffer);
  }
}
