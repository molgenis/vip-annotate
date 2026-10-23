package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.SizedIterator;

public class EnumSetAnnotationDatasetEncoder
    implements AnnotationDatasetEncoder<StringListAnnotation> {
  private final Map<String, Integer> enumValueToBitIndexMap;

  public EnumSetAnnotationDatasetEncoder(String[] enumValues) {
    // TODO perf: create map with known size
    this.enumValueToBitIndexMap =
        IntStream.range(0, enumValues.length)
            .boxed()
            .collect(Collectors.toMap(i -> enumValues[i], i -> i));
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
