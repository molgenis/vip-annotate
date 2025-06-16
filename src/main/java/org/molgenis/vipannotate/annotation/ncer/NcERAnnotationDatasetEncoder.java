package org.molgenis.vipannotate.annotation.ncer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetEncoder;
import org.molgenis.vipannotate.annotation.ContigPosAnnotation;
import org.molgenis.vipannotate.annotation.GenomePartition;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class NcERAnnotationDatasetEncoder implements AnnotationDatasetEncoder<ContigPosAnnotation> {
  private static final int BUFFER_ANNOTATIONS = (1 << GenomePartition.NR_POS_BITS);
  private static final int BUFFER_SIZE = BUFFER_ANNOTATIONS * Short.BYTES;

  @NonNull private final NcERAnnotationDataCodec ncERAnnotationDataCodec;

  @Override
  public MemoryBuffer encode(SizedIterator<ContigPosAnnotation> annotationIterator) {
    short nullScore = ncERAnnotationDataCodec.encode(null);
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(BUFFER_SIZE);
    if (nullScore != 0) {
      for (int i = 0; i < BUFFER_SIZE; i++) {
        memoryBuffer.putInt16(i, nullScore);
      }
    }

    annotationIterator.forEachRemaining(
        locusAnnotation -> {
          Double score = locusAnnotation.annotation();
          short encodedScore = ncERAnnotationDataCodec.encode(score);
          int pos = locusAnnotation.start();
          int index = GenomePartition.calcPosInBin(pos);
          memoryBuffer.putInt16(index, encodedScore);
        });

    return memoryBuffer;
  }
}
