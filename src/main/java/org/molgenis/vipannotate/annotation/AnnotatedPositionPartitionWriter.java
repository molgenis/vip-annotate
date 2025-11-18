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

/**
 * Writes partitions of annotated genomic positions
 *
 * @param <T> type of genomic position
 * @param <U> type of genomic position annotation
 * @param <V> annotated genomic position typed by T and U
 */
@RequiredArgsConstructor
public class AnnotatedPositionPartitionWriter<
        T extends Position, U extends Annotation, V extends AnnotatedInterval<T, U>>
    implements AnnotatedIntervalPartitionWriter<T, U, V> {
  private final String annotationDataId;
  private final IndexedAnnotatedFeatureDatasetEncoder<U> annotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;
  @Nullable private MemoryBuffer scratchBuffer;

  @Override
  public void write(Partition<T, U, V> partition) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(
          "processing partition %s/%d", partition.key().contig().getName(), partition.key().bin());
    }

    // prepare
    SizedIterator<IndexedAnnotation<U>> intervalIt =
        createIndexedAnnotatedIntervalIterator(partition);
    int maxAnnotations = partition.calcMaxPos();

    // encode
    long encodedSize = annotationDatasetEncoder.calcEncodedSize(maxAnnotations);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    annotationDatasetEncoder.encode(intervalIt, maxAnnotations, memBuffer);

    // write
    binaryPartitionWriter.write(
        partition.key(), annotationDataId, Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private SizedIterator<IndexedAnnotation<U>> createIndexedAnnotatedIntervalIterator(
      Partition<T, U, V> partition) {
    PartitionKey partitionKey = partition.key();
    List<V> annotatedIntervals = partition.annotatedIntervals();

    return new SizedIterator<>(
        new TransformingIterator<>(
            annotatedIntervals.iterator(),
            annotation -> createIndexedAnnotatedInterval(partitionKey, annotation)),
        annotatedIntervals.size());
  }

  private IndexedAnnotation<U> createIndexedAnnotatedInterval(
      PartitionKey partitionKey, V annotatedFeature) {
    int partitionStart =
        Partition.getPartitionStart(partitionKey, annotatedFeature.getFeature().getStart());
    return new IndexedAnnotation<>(partitionStart, annotatedFeature.getAnnotation());
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
