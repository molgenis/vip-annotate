package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.EnumSetLogicalType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class EnumSetAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<StringListAnnotation> {
  private final EnumSetLogicalType enumSetLogicalType;
  private final AnnotationBlobReader blobReader;

  @Override
  public AnnotationDataset<StringListAnnotation> decode(PartitionKey partitionKey) {
    MemoryBuffer memBuffer = blobReader.read(partitionKey);

    return memBuffer != null
        ? index -> {
          if (index < 0) {
            throw new IllegalArgumentException();
          }
          String[] enumValues = enumSetLogicalType.values();

          int bitOffset = Math.multiplyExact(index, enumValues.length);
          int byteOffset = bitOffset >>> 3;
          int bitInByte = bitOffset & 7;

          List<String> values = new ArrayList<>();

          for (int enumIndex = 0; enumIndex < enumValues.length; enumIndex++) {
            int bitIndex = bitInByte + enumIndex;
            int byteIndex = byteOffset + (bitIndex >>> 3);
            int bit = bitIndex & 7;

            int encodedByte = memBuffer.getUnsignedByte(byteIndex);

            if ((encodedByte & (1 << bit)) != 0) {
              values.add(enumValues[enumIndex]);
            }
          }

          return new StringListAnnotation(values.toArray(String[]::new));
        }
        : EmptyAnnotationDataset.getInstance();
  }

  @Override
  public void close() {
    ClosableUtils.close(blobReader);
  }
}
