package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryEncodable;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexBigWriter<T extends SequenceVariant>
    implements MemoryEncodable<AnnotationIndex<T>> {
  private final MemoryBufferFactory memBufferFactory;

  @Override
  public MemoryEncoder writeTo(AnnotationIndex<T> object) {
    MemoryEncoder memEncoder = MemoryEncoder.wrap(memBufferFactory.newMemoryBuffer());
    writeInto(object, memEncoder);
    return memEncoder;
  }

  @Override
  public void writeInto(AnnotationIndex<T> index, MemoryEncoder memEncoder) {
    // FIXME ensure buffer capacity
    SequenceVariantAnnotationIndexBig<T> indexBig = getTyped(index);

    BigInteger[] encodedVariants = indexBig.getEncodedVariantsArray();
    memEncoder.putVarInt(encodedVariants.length);
    for (BigInteger encodedVariant : encodedVariants) {
      byte[] byteArray = encodedVariant.toByteArray();
      memEncoder.putByteArray(byteArray);
    }
  }

  // TODO dedup with Reader
  private SequenceVariantAnnotationIndexBig<T> getTyped(AnnotationIndex<T> annotationIndex) {
    if (!(annotationIndex instanceof SequenceVariantAnnotationIndexBig<T> indexBig)) {
      throw new IllegalArgumentException("index must be of type SequenceVariantAnnotationIndexBig");
    }
    return indexBig;
  }
}
