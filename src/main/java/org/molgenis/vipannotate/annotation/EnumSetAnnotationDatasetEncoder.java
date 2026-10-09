package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.molgenis.vipannotate.serialization.BinaryWriter;
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
  public long getEncodedSizeInBytes(int annotationCount) {
    return Math.ceilDivExact(enumValueToBitIndexMap.size() * annotationCount, Byte.SIZE);
  }

  @Override
  public void encode(SizedIterator<StringListAnnotation> annotationIt, BinaryWriter binaryWriter) {
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
        binaryWriter.writeByte((byte) currentByte);
        currentByte >>>= Byte.SIZE;
        bitsInCurrentByte -= Byte.SIZE;
      }
    }

    if (bitsInCurrentByte > 0) {
      binaryWriter.writeByte((byte) currentByte);
    }
  }
}
