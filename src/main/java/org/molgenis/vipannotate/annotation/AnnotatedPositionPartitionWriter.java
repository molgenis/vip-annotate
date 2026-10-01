package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.PartitionedMemoryWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

/**
 * Writes partitions of annotated genomic positions
 *
 * @param <T> type of genomic position
 * @param <U> type of genomic position annotation
 * @param <W> annotated genomic position typed by T and U
 */
// FIXME dedup with AnnotatedSequenceVariantPartitionWriter
@RequiredArgsConstructor
public class AnnotatedPositionPartitionWriter<
        T extends Position,
        U extends Annotation, // type of position annotation
        V extends Annotation, // type of position annotation part to write to partition
        W extends AnnotatedInterval<T, U>>
    implements AnnotatedIntervalPartitionWriter<T, U, W> {
  private final String annotationDataId;
  private final AnnotationDatasetEncoder<V> annotationDatasetEncoder;
  private final PartitionedMemoryWriter partitionedMemWriter;
  private final Function<W, V> annotationExtractor;

  @Nullable private MemoryBuffer scratchBuffer;

  @Override
  public void write(Partition<T, U, W> partition) {
    // prepare
    List<W> annotatedVariants = partition.annotatedIntervals();
    SizedIterator<V> annotationIt =
        new SizedIterator<>(
            new TransformingIterator<>(annotatedVariants.iterator(), annotationExtractor),
            annotatedVariants.size());

    // encode
    AtomicReference<MemoryEncoder> memEncoder = new AtomicReference<>();
    annotationDatasetEncoder.encodeInto(
        annotationIt,
        () -> {
          if (memEncoder.get() == null) {
            memEncoder.set(MemoryEncoder.autoGrowing(getScratchBuffer()));
          }
          return memEncoder.get();
        });

    // write
    if (memEncoder.get() != null) {
      partitionedMemWriter.write(
          annotationDataId,
          Compression.ZSTD,
          IoMode.DIRECT,
          requireNonNull(scratchBuffer),
          partition.key());
    }
  }

  private MemoryBuffer getScratchBuffer() {
    if (scratchBuffer == null) {
      scratchBuffer = MemoryBuffer.allocateHeap(1);
    } else {
      scratchBuffer.clear();
    }
    return scratchBuffer;
  }

  @Override
  public void close() {
    ClosableUtils.close(scratchBuffer);
  }
}
