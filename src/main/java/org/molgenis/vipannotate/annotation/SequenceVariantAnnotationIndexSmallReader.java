package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryDecodable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexSmallReader<T extends SequenceVariant>
    implements MemoryDecodable<AnnotationIndex<T>> {
  private final SequenceVariantEncoder<T> encoder;
  private final StreamVByte streamVByte;

  @Override
  public SequenceVariantAnnotationIndexSmall<T> readFrom(MemoryDecoder memDecoder) {
    int nrEncodedVariants = memDecoder.getVarInt();
    int[] encodedVariantsArray = new int[nrEncodedVariants];
    readIntoEncodedVariantsArray(memDecoder, encodedVariantsArray, nrEncodedVariants);
    return new SequenceVariantAnnotationIndexSmall<>(
        encoder, encodedVariantsArray, nrEncodedVariants);
  }

  @Override
  public void readInto(MemoryDecoder memDecoder, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexSmall<T> indexSmall = getTyped(index);

    int nrEncodedVariants = memDecoder.getVarInt();
    int[] encodedVariantsArray = indexSmall.getEncodedVariantsArray();
    if (encodedVariantsArray.length < nrEncodedVariants) {
      // TODO perf: store max index size in db and allocate once
      // has the additional advantage that it prevents late out-of-memory errors
      encodedVariantsArray = new int[nrEncodedVariants];
    }
    readIntoEncodedVariantsArray(memDecoder, encodedVariantsArray, nrEncodedVariants);
    indexSmall.reset(encodedVariantsArray, nrEncodedVariants);
  }

  private void readIntoEncodedVariantsArray(
      MemoryDecoder memDecoder, int[] encodedVariantsArray, int nrEncodedVariants) {
    MemoryBuffer memBuffer = memDecoder.unwrap();
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
