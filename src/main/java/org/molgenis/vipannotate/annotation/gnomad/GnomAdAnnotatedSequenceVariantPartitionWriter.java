package org.molgenis.vipannotate.annotation.gnomad;

import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Numbers;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class GnomAdAnnotatedSequenceVariantPartitionWriter
    implements AnnotatedIntervalPartitionWriter<
        SequenceVariant, GnomAdAnnotation, AnnotatedSequenceVariant<GnomAdAnnotation>> {
  private final GnomAdAnnotationDatasetEncoder gnomAdAnnotationDataSetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;
  @Nullable private MemoryBuffer scratchBuffer;

  @Override
  public void write(
      Partition<SequenceVariant, GnomAdAnnotation, AnnotatedSequenceVariant<GnomAdAnnotation>>
          partition) {
    PartitionKey partitionKey = partition.key();
    List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedFeatures =
        partition.annotatedIntervals();

    writeSource(partitionKey, annotatedFeatures);
    writeAf(partitionKey, annotatedFeatures);
    writeFaf95(partitionKey, annotatedFeatures);
    writeFaf99(partitionKey, annotatedFeatures);
    writeHn(partitionKey, annotatedFeatures);
    writeFilters(partitionKey, annotatedFeatures);
    writeCov(partitionKey, annotatedFeatures);
  }

  private void writeSource(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<GnomAdAnnotation.Source> sourceIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().source()),
            annotatedVariants.size());

    // encode
    long encodedSize = gnomAdAnnotationDataSetEncoder.calcEncodedSourcesSize(sourceIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    gnomAdAnnotationDataSetEncoder.encodeSources(sourceIt, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, "src", Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private void writeAf(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<@Nullable Double> afIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().af()),
            annotatedVariants.size());

    // encode
    long encodedSize = gnomAdAnnotationDataSetEncoder.calcEncodedAfSize(afIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    gnomAdAnnotationDataSetEncoder.encodeAf(afIt, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, "af", Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private void writeFaf95(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<Double> faf95It =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().faf95()),
            annotatedVariants.size());

    // encode
    long encodedSize = gnomAdAnnotationDataSetEncoder.calcEncodedFaf95Size(faf95It);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    gnomAdAnnotationDataSetEncoder.encodeFaf95(faf95It, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, "faf95", Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private void writeFaf99(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<Double> faf99It =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().faf99()),
            annotatedVariants.size());

    // encode
    long encodedSize = gnomAdAnnotationDataSetEncoder.calcEncodedFaf99Size(faf99It);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    gnomAdAnnotationDataSetEncoder.encodeFaf99(faf99It, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, "faf99", Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private void writeHn(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<Integer> hnIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().hn()),
            annotatedVariants.size());

    // encode
    long encodedSize = gnomAdAnnotationDataSetEncoder.calcEncodedHnSize(hnIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    gnomAdAnnotationDataSetEncoder.encodeHn(hnIt, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, "hn", Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private void writeFilters(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<EnumSet<GnomAdAnnotation.Filter>> filtersIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().filters()),
            annotatedVariants.size());

    // encode
    long encodedSize = gnomAdAnnotationDataSetEncoder.calcEncodedFiltersSize(filtersIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    gnomAdAnnotationDataSetEncoder.encodeFilters(filtersIt, memBuffer);

    // write
    binaryPartitionWriter.write(
        partitionKey, "filters", Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private void writeCov(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<GnomAdAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<Double> covIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().cov()),
            annotatedVariants.size());

    // encode
    long encodedSize = gnomAdAnnotationDataSetEncoder.calcEncodedCovSize(covIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    gnomAdAnnotationDataSetEncoder.encodeCov(covIt, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, "cov", Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private MemoryBuffer getHeapBackedScratchBuffer(long minCapacity) {
    if (scratchBuffer == null) {
      scratchBuffer = MemoryBuffer.wrap(new byte[Math.toIntExact(minCapacity)]);
    } else {
      if (minCapacity > scratchBuffer.getCapacity()) {
        // ensureCapacity does not support heap backed buffers, create a new one
        scratchBuffer.close();
        scratchBuffer =
            MemoryBuffer.wrap(new byte[Math.toIntExact(Numbers.nextPowerOf2(minCapacity))]);
      } else {
        scratchBuffer.clear();
      }
    }
    return scratchBuffer;
  }

  @Override
  public void close() {
    if (scratchBuffer != null) {
      scratchBuffer.close();
    }
  }
}
