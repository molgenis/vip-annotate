package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

public class EnumAnnotationDatasetDecoder implements AnnotationDatasetDecoder<StringAnnotation> {
  private final String[] enumValues;
  private final int bitsPerAnnotation;

  public EnumAnnotationDatasetDecoder(EnumAnnotationSpec annotationSpec) {
    this.enumValues = annotationSpec.values();
    if (enumValues.length < 2) {
      throw new IllegalArgumentException();
    }
    this.bitsPerAnnotation = Integer.SIZE - Integer.numberOfLeadingZeros(enumValues.length - 1);
  }

  @Override
  public AnnotationDataset<StringAnnotation> decode(MemoryDecoderProvider decoderProvider) {
    MemoryDecoder memDecoder = decoderProvider.get();
    if (memDecoder == null) {
      return EmptyAnnotationDataset.getInstance();
    }

    return (index, annotationSupplier) -> {
      if (index < 0) {
        throw new IllegalArgumentException();
      }

      int enumIndex = readEnumIndex(memDecoder, index);
      if (enumIndex >= enumValues.length) {
        throw new IllegalArgumentException();
      }

      StringAnnotation annotation = annotationSupplier.get();
      annotation.reset(enumValues[enumIndex]);
      return annotation;
    };
  }

  private int readEnumIndex(MemoryDecoder memDecoder, int index) {
    int bitOffset = Math.multiplyExact(index, bitsPerAnnotation);
    int byteOffset = bitOffset >>> 3;
    int bitInByte = bitOffset & 7;

    int enumIndex = 0;
    int bitsRead = 0;

    while (bitsRead < bitsPerAnnotation) {
      int currentByte = memDecoder.getUnsignedByteAtIndex(byteOffset);
      int bitsAvailable = Byte.SIZE - bitInByte;
      int bitsToRead = Math.min(bitsAvailable, bitsPerAnnotation - bitsRead);

      int mask = (1 << bitsToRead) - 1;
      int bits = (currentByte >>> bitInByte) & mask;

      enumIndex |= bits << bitsRead;

      bitsRead += bitsToRead;
      byteOffset++;
      bitInByte = 0;
    }

    return enumIndex;
  }
}
