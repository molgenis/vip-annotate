package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

/**
 * Writes partitions of annotated positions
 *
 * @param <T> type of genomic interval
 * @param <U> type of genomic feature annotation
 * @param <V> annotated genomic feature typed by T and U
 */
@RequiredArgsConstructor
public class AnnotatedPositionPartitionWriter<
        T extends Position, U extends Annotation, V extends AnnotatedInterval<T, U>>
    implements AnnotatedIntervalPartitionWriter<T, U, V> {
  @NonNull private final String annotationDataId; // TODO refactor: move to partition writer
  @NonNull private final IndexedAnnotatedFeatureDatasetEncoder<U> annotationDatasetEncoder;
  @NonNull private final BinaryPartitionWriter binaryPartitionWriter;
  @NonNull private final FastaIndex fastaIndex;

  @Override
  public void write(Partition<T, U, V> partition) {
    Partition.Key partitionKey = partition.getKey();
    List<V> annotatedIntervals = partition.getAnnotatedIntervals();

    int maxAnnotations = calcMaxAnnotations(partitionKey);

    MemoryBuffer memoryBuffer =
        annotationDatasetEncoder.encode(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedIntervals.iterator(), annotation -> map(partitionKey, annotation)),
                annotatedIntervals.size()),
            maxAnnotations);
    binaryPartitionWriter.write(partitionKey, annotationDataId, memoryBuffer);
  }

  private IndexedAnnotation<U> map(Partition.Key partitionKey, V annotatedFeature) {
    int partitionStart =
        Partition.getPartitionStart(partitionKey, annotatedFeature.getFeature().getStart());
    return new IndexedAnnotation<>(partitionStart, annotatedFeature.getAnnotation());
  }

  private int calcMaxAnnotations(Partition.Key partitionKey) {
    int maxPosInContig =
        Math.toIntExact(
            fastaIndex
                .get(partitionKey.contig().getName())
                .length()); // FIXME use contig().getLength()
    boolean isLastBin = Partition.calcBin(maxPosInContig) == partitionKey.bin();

    int maxAnnotations;
    if (isLastBin) {
      maxAnnotations = Partition.calcPosInBin(maxPosInContig);
    } else {
      maxAnnotations = 1 << Partition.NR_POS_BITS;
    }
    return maxAnnotations;
  }
}
