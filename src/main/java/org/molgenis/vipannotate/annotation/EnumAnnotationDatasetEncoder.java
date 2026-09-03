package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.molgenis.vipannotate.annotation.spec.EnumLogicalType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;

public class EnumAnnotationDatasetEncoder implements AnnotationDatasetEncoder<StringAnnotation> {
  private final Map<String, Integer> enumValueToBitIndexMap;
  private final boolean nullable;
  private final int bitsPerAnnotation;

  public EnumAnnotationDatasetEncoder(EnumLogicalType logicalType) {
    String[] enumValues = logicalType.values();
    this.nullable = logicalType.nullable();

    // TODO perf: create map with known size
    this.enumValueToBitIndexMap =
        IntStream.range(0, enumValues.length)
            .boxed()
            .collect(Collectors.toMap(i -> enumValues[i], i -> nullable ? i + 1 : i));

    int valueCount = nullable ? enumValues.length + 1 : enumValues.length;
    this.bitsPerAnnotation = Integer.SIZE - Integer.numberOfLeadingZeros(valueCount - 1);
  }

  @Override
  public long getEncodedSizeInBytes(int annotationCount) {
    return Math.ceilDivExact((long) bitsPerAnnotation * annotationCount, Byte.SIZE);
  }

  @Override
  public void encode(
      SizedIterator<StringAnnotation> annotationIt, int maxAnnotations, MemoryBuffer memBuffer) {

    int currentByte = 0;
    int bitsInCurrentByte = 0;

    while (annotationIt.hasNext()) {
      StringAnnotation annotation = annotationIt.next();

      int enumIndex;

      if (annotation.value() == null) {
        if (!nullable) {
          throw new IllegalArgumentException("Null enum value is not allowed");
        }
        enumIndex = 0;
      } else {
        Integer mappedIndex = enumValueToBitIndexMap.get(annotation.value());
        if (mappedIndex == null) {
          throw new IllegalArgumentException(
              "Unknown enum value: %s".formatted(annotation.value()));
        }
        enumIndex = mappedIndex;
      }

      currentByte |= enumIndex << bitsInCurrentByte;
      bitsInCurrentByte += bitsPerAnnotation;

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
