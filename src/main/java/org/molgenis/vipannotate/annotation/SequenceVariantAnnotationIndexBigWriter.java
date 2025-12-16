package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexBigWriter<T extends SequenceVariant>
    implements MemoryBufferWriter<AnnotationIndex<T>> {
  private final MemoryBufferFactory memBufferFactory;

  @Override
  public MemoryBuffer writeTo(AnnotationIndex<T> object) {
    MemoryBuffer memBuffer = memBufferFactory.newMemoryBuffer();
    writeInto(object, memBuffer);
    return memBuffer;
  }

  @Override
  public void writeInto(AnnotationIndex<T> index, MemoryBuffer memBuffer) {
    // FIXME ensure buffer capacity
    SequenceVariantAnnotationIndexBig<T> indexBig = getTyped(index);

    BigInteger[] encodedVariants = indexBig.getEncodedVariantsArray();
    memBuffer.putVarUnsignedInt(encodedVariants.length);
    for (BigInteger encodedVariant : encodedVariants) {
      byte[] byteArray = encodedVariant.toByteArray();
      memBuffer.putByteArray(byteArray);
    }
  }

  // TODO dedeup with Reader
  private SequenceVariantAnnotationIndexBig<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexBig<T> indexBig)) {
      throw new IllegalArgumentException("index must be of type SequenceVariantAnnotationIndexBig");
    }
    return indexBig;
  }
}
