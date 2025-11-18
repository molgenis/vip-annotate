package org.molgenis.vipannotate.annotation.fathmmmkl;

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
public class FathmmMklAnnotatedSequenceVariantPartitionWriter
    implements AnnotatedIntervalPartitionWriter<
        SequenceVariant, FathmmMklAnnotation, AnnotatedSequenceVariant<FathmmMklAnnotation>> {
  private final FathmmMklAnnotationDatasetEncoder annotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;
  @Nullable private MemoryBuffer scratchBuffer;

  @Override
  public void write(
      Partition<SequenceVariant, FathmmMklAnnotation, AnnotatedSequenceVariant<FathmmMklAnnotation>>
          partition) {
    PartitionKey partitionKey = partition.key();
    List<AnnotatedSequenceVariant<FathmmMklAnnotation>> annotatedFeatures =
        partition.annotatedIntervals();

    writeScore(partitionKey, annotatedFeatures);
  }

  private void writeScore(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<FathmmMklAnnotation>> annotatedVariants) {
    // prepare
    SizedIterator<Double> scoreIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> annotatedVariant.getAnnotation().score()),
            annotatedVariants.size());

    // encode
    long encodedSize = annotationDatasetEncoder.calcEncodedGeneIdSize(scoreIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    annotationDatasetEncoder.encodeScores(scoreIt, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, "score", Compression.ZSTD, IoMode.DIRECT, memBuffer);
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
