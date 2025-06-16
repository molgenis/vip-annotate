package org.molgenis.vipannotate.annotation.phylop;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetEncoder;
import org.molgenis.vipannotate.annotation.ContigPosAnnotation;
import org.molgenis.vipannotate.annotation.GenomePartition;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class PhyloPAnnotationDatasetEncoder
    implements AnnotationDatasetEncoder<ContigPosAnnotation> {
  private static final int BUFFER_SIZE = (1 << GenomePartition.NR_POS_BITS) * Short.BYTES;

  @NonNull private final PhyloPAnnotationDataCodec phyloPAnnotationDataCodec;

  @Override
  public MemoryBuffer encode(SizedIterator<ContigPosAnnotation> annotationIterator) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(BUFFER_SIZE);

    annotationIterator.forEachRemaining(
        locusAnnotation -> {
          Double score = locusAnnotation.annotation();
          short encodedScore = phyloPAnnotationDataCodec.encode(score);
          int pos = locusAnnotation.start();
          int index = GenomePartition.calcPosInBin(pos);
          memoryBuffer.putInt16(index, encodedScore);
        });

    return memoryBuffer;
  }
}
