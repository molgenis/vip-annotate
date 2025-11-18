package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexSmallReader<T extends SequenceVariant>
    implements MemoryBufferReader<AnnotationIndex<T>> {
  private final SequenceVariantEncoder<T> encoder;
  private final StreamVByte streamVByte;

  @Override
  public SequenceVariantAnnotationIndexSmall<T> readFrom(MemoryBuffer memBuffer) {
    int nrEncodedVariants = memBuffer.getVarUnsignedInt();
    int[] encodedVariantsArray = new int[nrEncodedVariants];
    readIntoEncodedVariantsArray(memBuffer, encodedVariantsArray, nrEncodedVariants);
    return new SequenceVariantAnnotationIndexSmall<>(
        encoder, encodedVariantsArray, nrEncodedVariants);
  }

  @Override
  public void readInto(MemoryBuffer memoryBuffer, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexSmall<T> indexSmall = getTyped(index);

    int nrEncodedVariants = memoryBuffer.getVarUnsignedInt();
    int[] encodedVariantsArray = indexSmall.getEncodedVariantsArray();
    if (encodedVariantsArray.length < nrEncodedVariants) {
      // TODO perf: store max index size in db and allocate once
      // has the additional advantage that it prevents late out-of-memory errors
      encodedVariantsArray = new int[nrEncodedVariants];
    }
    readIntoEncodedVariantsArray(memoryBuffer, encodedVariantsArray, nrEncodedVariants);
    indexSmall.reset(encodedVariantsArray, nrEncodedVariants);
  }

  private void readIntoEncodedVariantsArray(
      MemoryBuffer memBuffer, int[] encodedVariantsArray, int nrEncodedVariants) {
    long pos = memBuffer.getPosition();
    long nrBytesRead =
        streamVByte.deltaDecode(
            memBuffer.getMemSegment(),
            MemorySegment.ofArray(encodedVariantsArray),
            nrEncodedVariants);
    memBuffer.setPosition(pos + nrBytesRead);
  }

  private SequenceVariantAnnotationIndexSmall<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexSmall<T> indexSmall)) {
      throw new IllegalArgumentException(
          "index must be of type SequenceVariantAnnotationIndexSmall");
    }
    return indexSmall;
  }
}
