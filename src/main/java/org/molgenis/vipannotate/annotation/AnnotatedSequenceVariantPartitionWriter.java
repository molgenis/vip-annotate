package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Numbers;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class AnnotatedSequenceVariantPartitionWriter<
        T extends SequenceVariant, U extends Annotation, V extends AnnotatedInterval<T, U>>
    implements AnnotatedIntervalPartitionWriter<T, U, V> {
  private final String annotationDataId;
  private final AnnotationDatasetEncoder<U> annotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;
  @Nullable private MemoryBuffer scratchBuffer;

  @Override
  public void write(Partition<T, U, V> partition) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(
          "processing partition %s/%d", partition.key().contig().getName(), partition.key().bin());
    }

    // prepare
    List<V> annotatedVariants = partition.annotatedIntervals();
    SizedIterator<U> annotationIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(), AnnotatedInterval::getAnnotation),
            annotatedVariants.size());

    // encode
    long encodedSize = annotationIt.getSize() * Byte.BYTES; // FIXME hardcoded
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    annotationDatasetEncoder.encode(annotationIt, -1, memBuffer); // FIXME remove maxAnnotations

    // write
    binaryPartitionWriter.write(
        annotationDataId, Compression.ZSTD, IoMode.DIRECT, memBuffer, partition.key());
  }

  // FIXME dedup with AnnotationPositionVariantPartitionWriter
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
