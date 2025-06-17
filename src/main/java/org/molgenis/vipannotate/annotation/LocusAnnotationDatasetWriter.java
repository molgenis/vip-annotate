package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.util.SizedIterable;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class LocusAnnotationDatasetWriter<T extends LocusAnnotation<U>, U>
    implements AnnotationDatasetWriter<T> {
  private static final int MAX_ANNOTATIONS = (1 << GenomePartition.NR_POS_BITS);

  @NonNull private final String annotationDataId;

  @NonNull
  private final AnnotationDatasetEncoder<IndexedLocusAnnotation<T, U>> annotationDatasetEncoder;

  @NonNull private final GenomePartitionDataWriter genomePartitionDataWriter;
  @NonNull private final FastaIndex fastaIndex;

  @Override
  public void write(GenomePartitionKey genomePartitionKey, SizedIterable<T> annotations) {
    int maxAnnotations = calcMaxAnnotations(genomePartitionKey);

    MemoryBuffer memoryBuffer =
        annotationDatasetEncoder.encode(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotations.iterator(), annotation -> map(genomePartitionKey, annotation)),
                annotations.getSize()),
            maxAnnotations); // FIXME last bin can be larger than contig, use fasta index
    genomePartitionDataWriter.write(genomePartitionKey, annotationDataId, memoryBuffer);
  }

  private IndexedLocusAnnotation<T, U> map(GenomePartitionKey genomePartitionKey, T annotation) {
    int partitionStart = GenomePartition.getPartitionStart(genomePartitionKey, annotation.start());
    return new IndexedLocusAnnotation<>(partitionStart, annotation);
  }

  private int calcMaxAnnotations(GenomePartitionKey genomePartitionKey) {
    int maxPosInContig = Math.toIntExact(fastaIndex.get(genomePartitionKey.contig()).length());
    boolean isLastBin = GenomePartition.calcBin(maxPosInContig) == genomePartitionKey.bin();

    int maxAnnotations;
    if (isLastBin) {
      maxAnnotations = GenomePartition.calcPosInBin(maxPosInContig);
    } else {
      maxAnnotations = 1 << GenomePartition.NR_POS_BITS;
    }
    return maxAnnotations;
  }
}
