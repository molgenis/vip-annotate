package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.molgenis.vipannotate.annotation.spec.EnumSetLogicalType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

public class EnumSetAnnotationDatasetEncoder
    implements AnnotationDatasetEncoder<StringListAnnotation> {
  private final Map<String, Integer> enumValueToBitIndexMap;

  public EnumSetAnnotationDatasetEncoder(EnumSetLogicalType logicalType) {
    String[] enumValues = logicalType.values();
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
  public void encode(
      SizedIterator<StringListAnnotation> annotationIt,
      int maxAnnotations,
      MemoryBuffer memBuffer) {
    int currentByte = 0;
    int bitsInCurrentByte = 0;

    while (annotationIt.hasNext()) {
      StringListAnnotation annotation = annotationIt.next();

      for (String value : annotation.values()) {
        Integer bitIndex = enumValueToBitIndexMap.get(value);
        if (bitIndex == null) {
          throw new IllegalArgumentException("Unknown enum value: %s".formatted(value));
        }

        currentByte |= 1 << (bitsInCurrentByte + bitIndex);
      }

      bitsInCurrentByte += enumValueToBitIndexMap.size();

      while (bitsInCurrentByte >= Byte.SIZE) {
        memBuffer.putByteUnchecked((byte) currentByte);
        currentByte >>>= Byte.SIZE;
        bitsInCurrentByte -= Byte.SIZE;
      }
    }

    if (bitsInCurrentByte > 0) {
      memBuffer.putByteUnchecked((byte) currentByte);
    }
  }
}
