package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.SizedIterator;

public class EnumAnnotationDatasetEncoder implements AnnotationDatasetEncoder<StringAnnotation> {
  private final Map<String, Integer> enumValueToBitIndexMap;
  private final boolean nullable;
  private final int bitsPerAnnotation;

  public EnumAnnotationDatasetEncoder(String[] enumValues, boolean nullable) {
    this.nullable = nullable;

    // TODO perf: create map with known size
    this.enumValueToBitIndexMap =
        IntStream.range(0, enumValues.length)
            .boxed()
            .collect(Collectors.toMap(i -> enumValues[i], i -> this.nullable ? i + 1 : i));

    int valueCount = this.nullable ? enumValues.length + 1 : enumValues.length;
    this.bitsPerAnnotation = Integer.SIZE - Integer.numberOfLeadingZeros(valueCount - 1);
  }

  @Override
  public void encodeInto(
      SizedIterator<StringAnnotation> annotationIt, MemoryEncoderProvider encoderProvider) {
    MemoryEncoder memEncoder = encoderProvider.get();

    int currentByte = 0;
    int bitsInCurrentByte = 0;

    while (annotationIt.hasNext()) {
      StringAnnotation annotation = annotationIt.next();

      int enumIndex;

      if (annotation.getValue() == null) {
        if (!nullable) {
          throw new IllegalArgumentException("Null enum value is not allowed");
        }
        enumIndex = 0;
      } else {
        Integer mappedIndex = enumValueToBitIndexMap.get(annotation.getValue());
        if (mappedIndex == null) {
          throw new IllegalArgumentException(
              "Unknown enum value: %s".formatted(annotation.getValue()));
        }
        enumIndex = mappedIndex;
      }

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
