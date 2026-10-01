package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class EnumSetAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<StringListAnnotation> {
  private final EnumSetAnnotationSpec enumSetAnnotationSpec;

  @Override
  public AnnotationDataset<StringListAnnotation> decode(MemoryDecoderProvider decoderProvider) {
    MemoryDecoder memDecoder = decoderProvider.get();
    if (memDecoder == null) {
      return EmptyAnnotationDataset.getInstance();
    }

    return (index, annotationSupplier) -> {
      if (index < 0) {
        throw new IllegalArgumentException();
      }
      String[] enumValues = enumSetAnnotationSpec.values();

      int bitOffset = Math.multiplyExact(index, enumValues.length);
      int byteOffset = bitOffset >>> 3;
      int bitInByte = bitOffset & 7;

      // FIXME reuse StringListAnnotationList.values
      List<String> values = new ArrayList<>();

      for (int enumIndex = 0; enumIndex < enumValues.length; enumIndex++) {
        int bitIndex = bitInByte + enumIndex;
        int byteIndex = byteOffset + (bitIndex >>> 3);
        int bit = bitIndex & 7;

        int encodedByte = memDecoder.getUnsignedByteAtIndex(byteIndex);

        if ((encodedByte & (1 << bit)) != 0) {
          values.add(enumValues[enumIndex]);
        }
      }
      StringListAnnotation stringListAnnotation = annotationSupplier.get();
      stringListAnnotation.reset(values.toArray(String[]::new));
      return stringListAnnotation;
    };
  }
}
