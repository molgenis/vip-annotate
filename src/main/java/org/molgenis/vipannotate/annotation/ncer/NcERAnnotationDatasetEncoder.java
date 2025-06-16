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
  @NonNull private final NcERAnnotationDataCodec ncERAnnotationDataCodec;

  @Override
  public MemoryBuffer encode(SizedIterator<ContigPosAnnotation> annotationIterator) {
    MemoryBuffer memoryBuffer =
        MemoryBuffer.newHeapBuffer(((int) Math.pow(2, GenomePartition.NR_POS_BITS)) * Short.BYTES);

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
