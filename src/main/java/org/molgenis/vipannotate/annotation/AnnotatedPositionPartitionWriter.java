package org.molgenis.vipannotate.annotation;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.BinaryWriter;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.ScratchMemoryBuffer;
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
@RequiredArgsConstructor
public class AnnotatedPositionPartitionWriter<
        T extends Position,
        U extends Annotation, // type of position annotation
        V extends Annotation, // type of position annotation part to write to partition
        W extends AnnotatedInterval<T, U>>
    implements AnnotatedIntervalPartitionWriter<T, U, W> {
  private final ScratchMemoryBuffer scratchBuffer = new ScratchMemoryBuffer();

  private final String annotationDataId;
  private final AnnotationDatasetEncoder<V> annotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;
  private final Function<W, V> annotationExtractor;

  @Override
  public void write(Partition<T, U, W> partition) {
    // prepare
    List<W> annotatedVariants = partition.annotatedIntervals();
    SizedIterator<V> annotationIt =
        new SizedIterator<>(
            new TransformingIterator<>(annotatedVariants.iterator(), annotationExtractor),
            annotatedVariants.size());

    // encode
    long encodedAnnotationByteSize =
        annotationDatasetEncoder.getEncodedSizeInBytes(annotationIt.getSize());
    MemoryBuffer memBuffer = scratchBuffer.get(encodedAnnotationByteSize);
    annotationDatasetEncoder.encode(annotationIt, BinaryWriter.fixed(memBuffer));

    // write
    binaryPartitionWriter.write(
        annotationDataId, Compression.ZSTD, IoMode.DIRECT, memBuffer, partition.key());
  }

  @Override
  public void close() {
    ClosableUtils.close(scratchBuffer);
  }
}
