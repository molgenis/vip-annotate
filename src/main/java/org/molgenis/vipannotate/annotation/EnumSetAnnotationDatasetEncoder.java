package org.molgenis.vipannotate.annotation;

import java.util.Map;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.Maps;
import org.molgenis.vipannotate.util.SizedIterator;

public class EnumSetAnnotationDatasetEncoder
    implements AnnotationDatasetEncoder<StringListAnnotation> {
  private final Map<String, Integer> enumValueToBitIndexMap;

  public EnumSetAnnotationDatasetEncoder(String[] enumValues) {
    this.enumValueToBitIndexMap = Maps.newHashMapWithExpectedSize(enumValues.length);
    for (int i = 0; i < enumValues.length; i++) {
      this.enumValueToBitIndexMap.put(enumValues[i], i);
    }
  }

  @Override
  public void encodeInto(
      SizedIterator<StringListAnnotation> annotationIt, MemoryEncoderProvider encoderProvider) {
    MemoryEncoder memEncoder = encoderProvider.get();

    int currentByte = 0;
    int bitsInCurrentByte = 0;

    while (annotationIt.hasNext()) {
      StringListAnnotation annotation = annotationIt.next();

      for (String value : annotation.getValues()) {
        Integer bitIndex = enumValueToBitIndexMap.get(value);
        if (bitIndex == null) {
          throw new IllegalArgumentException("Unknown enum value: %s".formatted(value));
        }

        currentByte |= 1 << (bitsInCurrentByte + bitIndex);
      }

      bitsInCurrentByte += enumValueToBitIndexMap.size();

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
