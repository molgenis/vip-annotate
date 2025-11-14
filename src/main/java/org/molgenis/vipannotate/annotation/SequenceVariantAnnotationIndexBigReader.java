package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexBigReader<T extends SequenceVariant>
    implements MemoryBufferReader<AnnotationIndex<T>> {
  private final SequenceVariantEncoder<T> encoder;

  @Override
  public SequenceVariantAnnotationIndexBig<T> readFrom(MemoryBuffer memoryBuffer) {
    int length = memoryBuffer.getVarUnsignedInt();
    BigInteger[] encodedVariants = new BigInteger[length];
    readIntoEncodedVariantsArray(memoryBuffer, encodedVariants, length);
    return new SequenceVariantAnnotationIndexBig<>(encoder, encodedVariants);
  }

  @Override
  public void readInto(MemoryBuffer memoryBuffer, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexBig<T> indexBig = getTyped(index);

    int nrEncodedVariants = memoryBuffer.getVarUnsignedInt();
    BigInteger[] bigIntegerArray = indexBig.getEncodedVariantsArray();
    if (bigIntegerArray.length < nrEncodedVariants) {
      bigIntegerArray = new BigInteger[nrEncodedVariants];
    }
    readIntoEncodedVariantsArray(memoryBuffer, bigIntegerArray, nrEncodedVariants);
    indexBig.reset(bigIntegerArray, nrEncodedVariants);
  }

  private static void readIntoEncodedVariantsArray(
      MemoryBuffer memoryBuffer, BigInteger[] bigIntegerArray, int nrEncodedVariants) {
    // TODO perf: replace BigInteger[] with byte[] flatData, int[] offsets and custom binary search
    // annotation of one WGS allocates ~4GB memory
    for (int i = 0; i < nrEncodedVariants; i++) {
      bigIntegerArray[i] = new BigInteger(memoryBuffer.getByteArray());
    }
  }

  private SequenceVariantAnnotationIndexBig<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexBig<T> indexBig)) {
      throw new IllegalArgumentException("index must be of type SequenceVariantAnnotationIndexBig");
    }
    return indexBig;
  }
}
