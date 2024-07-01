package org.molgenis.vipannotate.annotation;

import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexSmallSerializer<T extends SequenceVariant>
    implements BinarySerializer<AnnotationIndex<T>> {
  private final SequenceVariantEncoder<T> encoder;
  private final StreamVByte streamVByte;

  @Override
  public void writeTo(MemoryBuffer memoryBuffer, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexSmall<T> indexSmall = getTyped(index);
    int[] encodedVariantsArray = indexSmall.getEncodedVariantsArray();
    long maxCompressedBytes = streamVByte.maxCompressedBytes(encodedVariantsArray.length);

    memoryBuffer.ensureCapacity(
        memoryBuffer.getPosition() + MemoryBuffer.VAR_INT_MAX_BYTE_SIZE + maxCompressedBytes);

    memoryBuffer.putVarUnsignedIntUnchecked(encodedVariantsArray.length);
    long nrBytesWritten =
        streamVByte.deltaEncode(
            MemorySegment.ofArray(encodedVariantsArray),
            memoryBuffer.asMemorySegment(memoryBuffer.getPosition()),
            encodedVariantsArray.length);
    memoryBuffer.setPosition(memoryBuffer.getPosition() + nrBytesWritten);
  }

  @Override
  public SequenceVariantAnnotationIndexSmall<T> readFrom(MemoryBuffer memoryBuffer) {
    int nrEncodedVariants = memoryBuffer.getVarUnsignedInt();
    int[] encodedVariantsArray = new int[nrEncodedVariants];
    readIntoEncodedVariantsArray(memoryBuffer, encodedVariantsArray, nrEncodedVariants);
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

  @Override
  public SequenceVariantAnnotationIndexSmall<T> readEmpty() {
    return SequenceVariantAnnotationIndexSmallFactory.create();
  }

  private void readIntoEncodedVariantsArray(
      MemoryBuffer byteMemoryBuffer, int[] encodedVariantsArray, int nrEncodedVariants) {
    long nrBytesRead =
        streamVByte.deltaDecode(
            byteMemoryBuffer.asMemorySegment(byteMemoryBuffer.getPosition()),
            MemorySegment.ofArray(encodedVariantsArray),
            nrEncodedVariants);
    byteMemoryBuffer.setPosition(byteMemoryBuffer.getPosition() + nrBytesRead);
  }

  private SequenceVariantAnnotationIndexSmall<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexSmall<T> indexSmall)) {
      throw new IllegalArgumentException(
          "index must be of type SequenceVariantAnnotationIndexSmall");
    }
    return indexSmall;
  }
}
