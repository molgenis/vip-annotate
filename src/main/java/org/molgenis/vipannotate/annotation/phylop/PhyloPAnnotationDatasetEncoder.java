package org.molgenis.vipannotate.annotation.phylop;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetEncoder;
import org.molgenis.vipannotate.annotation.ContigPosAnnotation;
import org.molgenis.vipannotate.annotation.GenomePartition;
import org.molgenis.vipannotate.annotation.IndexedLocusAnnotation;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class PhyloPAnnotationDatasetEncoder
    implements AnnotationDatasetEncoder<IndexedLocusAnnotation<ContigPosAnnotation, Double>> {
  private static final int BUFFER_ANNOTATIONS = (1 << GenomePartition.NR_POS_BITS);
  private static final int BUFFER_SIZE = BUFFER_ANNOTATIONS * Short.BYTES;

  @NonNull private final PhyloPAnnotationDataCodec phyloPAnnotationDataCodec;

  @Override
  public MemoryBuffer encode(
      SizedIterator<IndexedLocusAnnotation<ContigPosAnnotation, Double>> annotationIterator) {
    short nullScore = phyloPAnnotationDataCodec.encode(null);
    MemoryBuffer memoryBuffer =
        MemoryBuffer.newHeapBuffer(BUFFER_SIZE); // FIXME last bin can be larger than contig
    if (nullScore != 0) {
      for (int i = 0; i < BUFFER_SIZE; i++) {
        memoryBuffer.putInt16(i, nullScore);
      }
    }

    annotationIterator.forEachRemaining(
        locusAnnotation -> {
          Double score = locusAnnotation.annotation().score();
          short encodedScore = phyloPAnnotationDataCodec.encode(score);
          int index = locusAnnotation.index();
          memoryBuffer.putInt16(index, encodedScore);
        });

    return memoryBuffer;
  }
}
