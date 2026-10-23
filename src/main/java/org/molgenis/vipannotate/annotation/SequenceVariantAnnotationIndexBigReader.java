package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryDecodable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexBigReader<T extends SequenceVariant>
    implements MemoryDecodable<AnnotationIndex<T>> {
  private final SequenceVariantEncoder<T> encoder;

  @Override
  public SequenceVariantAnnotationIndexBig<T> readFrom(MemoryDecoder memDecoder) {
    int length = memDecoder.getVarInt();
    BigInteger[] encodedVariants = new BigInteger[length];
    readIntoEncodedVariantsArray(memDecoder, encodedVariants, length);
    return new SequenceVariantAnnotationIndexBig<>(encoder, encodedVariants);
  }

  @Override
  public void readInto(MemoryDecoder memDecoder, AnnotationIndex<T> index) {
    SequenceVariantAnnotationIndexBig<T> indexBig = getTyped(index);

    int nrEncodedVariants = memDecoder.getVarInt();
    BigInteger[] bigIntegerArray = indexBig.getEncodedVariantsArray();
    if (bigIntegerArray.length < nrEncodedVariants) {
      bigIntegerArray = new BigInteger[nrEncodedVariants];
    }
    readIntoEncodedVariantsArray(memDecoder, bigIntegerArray, nrEncodedVariants);
    indexBig.reset(bigIntegerArray, nrEncodedVariants);
  }

  private static void readIntoEncodedVariantsArray(
      MemoryDecoder memDecoder, BigInteger[] bigIntegerArray, int nrEncodedVariants) {
    // TODO perf: replace BigInteger[] with byte[] flatData, int[] offsets and custom binary search
    // annotation of one WGS allocates ~4GB memory
    for (int i = 0; i < nrEncodedVariants; i++) {
      bigIntegerArray[i] = new BigInteger(memDecoder.getByteArray());
    }
  }

  private SequenceVariantAnnotationIndexBig<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexBig<T> indexBig)) {
      throw new IllegalArgumentException("index must be of type SequenceVariantAnnotationIndexBig");
    }
    return indexBig;
  }
}
