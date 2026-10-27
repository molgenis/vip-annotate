package org.molgenis.vipannotate.annotation;

import java.util.Map;
import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.Maps;
import org.molgenis.vipannotate.util.SizedIterator;

public class EnumAnnotationDatasetEncoder implements AnnotationDatasetEncoder<StringAnnotation> {
  private final Map<String, Integer> enumValueToBitIndexMap;
  private final int bitsPerAnnotation;

  public EnumAnnotationDatasetEncoder(EnumAnnotationSpec annotationSpec) {
    String[] enumValues = annotationSpec.values();

    this.enumValueToBitIndexMap = Maps.newHashMapWithExpectedSize(enumValues.length);
    for (int i = 0; i < enumValues.length; i++) {
      this.enumValueToBitIndexMap.put(enumValues[i], i);
    }
    this.bitsPerAnnotation = Integer.SIZE - Integer.numberOfLeadingZeros(enumValues.length - 1);
  }

  @Override
  public void encodeInto(
      SizedIterator<StringAnnotation> annotationIt, MemoryEncoderProvider encoderProvider) {
    MemoryEncoder memEncoder = encoderProvider.get();

    int currentByte = 0;
    int bitsInCurrentByte = 0;

    while (annotationIt.hasNext()) {
      StringAnnotation annotation = annotationIt.next();
      if (annotation.getValue() == null) {
        throw new IllegalArgumentException();
      }

      Integer mappedIndex = enumValueToBitIndexMap.get(annotation.getValue());
      if (mappedIndex == null) {
        throw new IllegalArgumentException(
            "Unknown enum value: %s".formatted(annotation.getValue()));
      }
      int enumIndex = mappedIndex;

      currentByte |= enumIndex << bitsInCurrentByte;
      bitsInCurrentByte += bitsPerAnnotation;

      while (bitsInCurrentByte >= Byte.SIZE) {
        memEncoder.putByte((byte) currentByte);
        currentByte >>>= Byte.SIZE;
        bitsInCurrentByte -= Byte.SIZE;
      }
    }

    if (bitsInCurrentByte > 0) {
      memEncoder.putByte((byte) currentByte);
    }
  }
}
