package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import java.util.List;
import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

/// Decodes enum sets with [1,32] enum values
public class EnumSetAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<StringListAnnotation> {
  private final String[] enumValues;
  private final int bitsPerAnnotation;

  public EnumSetAnnotationDatasetDecoder(EnumSetAnnotationSpec annotationSpec) {
    this.enumValues = annotationSpec.values();
    if (enumValues.length < 1 || enumValues.length >= 32) {
      throw new IllegalArgumentException();
    }
    this.bitsPerAnnotation = enumValues.length;
  }

  @Override
  public AnnotationDataset<StringListAnnotation> decode(MemoryDecoderProvider decoderProvider) {
    MemoryDecoder memDecoder = decoderProvider.get();
    if (memDecoder == null) {
      return EmptyAnnotationDataset.getInstance();
    }

    return (index, annotationSupplier) -> {
      validateNonNegative(index);

      int bitOffset = Math.multiplyExact(index, bitsPerAnnotation);
      int byteOffset = bitOffset >>> 3;
      int bitInByte = bitOffset & 7;

      StringListAnnotation stringListAnnotation = annotationSupplier.get();
      stringListAnnotation.reset();

      List<String> values = stringListAnnotation.getValues();
      for (int enumIndex = 0; enumIndex < bitsPerAnnotation; enumIndex++) {
        int bitIndex = bitInByte + enumIndex;
        int byteIndex = byteOffset + (bitIndex >>> 3);
        int bit = bitIndex & 7;

        int encodedByte = memDecoder.getUnsignedByteAtIndex(byteIndex);

        if ((encodedByte & (1 << bit)) != 0) {
          values.add(enumValues[enumIndex]);
        }
      }

      return stringListAnnotation;
    };
  }
}
