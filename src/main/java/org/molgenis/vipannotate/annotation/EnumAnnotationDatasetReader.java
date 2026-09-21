package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.ResolvedEnumAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class EnumAnnotationDatasetReader implements AnnotationDatasetDecoder<StringAnnotation> {
  private final ResolvedEnumAnnotationSpec enumAnnotationSpec;
  private final AnnotationBlobReader blobReader;

  @Override
  public AnnotationDataset<StringAnnotation> decode(PartitionKey partitionKey) {
    MemoryBuffer memBuffer = blobReader.read(partitionKey);

    return memBuffer != null
        ? index -> {
          if (index < 0) {
            throw new IllegalArgumentException();
          }

          String[] enumValues = enumAnnotationSpec.values();
          int bitsPerAnnotation = getBitsPerAnnotation();

          if (bitsPerAnnotation == 0) {
            return new StringAnnotation(enumValues[0]);
          }

          int bitOffset = Math.multiplyExact(index, bitsPerAnnotation);
          int byteOffset = bitOffset >>> 3;
          int bitInByte = bitOffset & 7;

          int value = 0;
          int bitsRead = 0;

          while (bitsRead < bitsPerAnnotation) {
            int currentByte = memBuffer.getUnsignedByte(byteOffset);
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
            return new StringAnnotation(null);
          }

          int enumIndex = enumAnnotationSpec.nullable() ? value - 1 : value;

          if (enumIndex < 0 || enumIndex >= enumValues.length) {
            throw new IllegalArgumentException("Invalid enum index: %d".formatted(value));
          }

          return new StringAnnotation(enumValues[enumIndex]);
        }
        : EmptyAnnotationDataset.getInstance();
  }

  @Override
  public void close() {
    ClosableUtils.close(blobReader);
  }

  private int getBitsPerAnnotation() {
    int enumValueCount = enumAnnotationSpec.values().length;
    int valueCount = enumAnnotationSpec.nullable() ? enumValueCount + 1 : enumValueCount;

    return Integer.SIZE - Integer.numberOfLeadingZeros(valueCount - 1);
  }
}
