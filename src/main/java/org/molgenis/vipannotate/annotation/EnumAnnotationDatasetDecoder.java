package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class EnumAnnotationDatasetDecoder implements AnnotationDatasetDecoder<StringAnnotation> {
  private final EnumAnnotationSpec enumAnnotationSpec;

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

      String[] enumValues = enumAnnotationSpec.values();
      int bitsPerAnnotation = getBitsPerAnnotation();

      StringAnnotation stringAnnotation = annotationSupplier.get();
      if (bitsPerAnnotation == 0) {
        stringAnnotation.reset(enumValues[0]);
        return stringAnnotation;
      }

      int bitOffset = Math.multiplyExact(index, bitsPerAnnotation);
      int byteOffset = bitOffset >>> 3;
      int bitInByte = bitOffset & 7;

      int value = 0;
      int bitsRead = 0;

      while (bitsRead < bitsPerAnnotation) {
        int currentByte = memDecoder.getUnsignedByteAtIndex(byteOffset);
        int bitsAvailable = Byte.SIZE - bitInByte;
        int bitsToRead = Math.min(bitsAvailable, bitsPerAnnotation - bitsRead);

        int mask = (1 << bitsToRead) - 1;
        int bits = (currentByte >>> bitInByte) & mask;

        value |= bits << bitsRead;

        bitsRead += bitsToRead;
        byteOffset++;
        bitInByte = 0;
      }

      if (enumAnnotationSpec.nullable() && value == 0) {
        stringAnnotation.reset(null);
        return stringAnnotation;
      }

      int enumIndex = enumAnnotationSpec.nullable() ? value - 1 : value;

      if (enumIndex < 0 || enumIndex >= enumValues.length) {
        throw new IllegalArgumentException("Invalid enum index: %d".formatted(value));
      }

      stringAnnotation.reset(enumValues[enumIndex]);
      return stringAnnotation;
    };
  }

  private int getBitsPerAnnotation() {
    int enumValueCount = enumAnnotationSpec.values().length;
    int valueCount = enumAnnotationSpec.nullable() ? enumValueCount + 1 : enumValueCount;

    return Integer.SIZE - Integer.numberOfLeadingZeros(valueCount - 1);
  }
}
