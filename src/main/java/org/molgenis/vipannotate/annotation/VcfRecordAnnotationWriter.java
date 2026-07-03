package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.Info;
import org.molgenis.vipannotate.format.vcf.VcfInfoSubfieldValueBuilder;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

@RequiredArgsConstructor
public class VcfRecordAnnotationWriter<T extends ScalarAnnotation> {
  private final String infoId;
  private final VcfInfoSubfieldValueBuilder reusableVcfInfoBuilder;

  public VcfRecordAnnotationWriter(String infoId) {
    this(infoId, new VcfInfoSubfieldValueBuilder());
  }

  public void appendAltAnnotation(@Nullable T altAnnotation) {
    if (altAnnotation == null) {
      reusableVcfInfoBuilder.appendValueMissing();
      return;
    }

    switch (altAnnotation) {
      case ScalarAnnotation.DoubleAnnotation doubleAnnotation ->
          appendDoubleAnnotation(doubleAnnotation);
      case ScalarAnnotation.NullableDoubleAnnotation nullableDoubleAnnotation ->
          appendNullableDoubleAnnotation(nullableDoubleAnnotation);
      default -> throw new RuntimeException(); // FIXME
    }
  }

  private void appendNullableDoubleAnnotation(
      ScalarAnnotation.NullableDoubleAnnotation nullableDoubleAnnotation) {
    if (nullableDoubleAnnotation.isNull()) {
      reusableVcfInfoBuilder.appendValueMissing();
    } else {
      reusableVcfInfoBuilder.appendValue(
          nullableDoubleAnnotation.getValue(), 3); // FIXME make configurable
    }
  }

  private void appendDoubleAnnotation(ScalarAnnotation.DoubleAnnotation doubleAnnotation) {
    reusableVcfInfoBuilder.appendValue(doubleAnnotation.getValue(), 3); // FIXME make configurable
  }

  public void writeInfoSubField(VcfRecord vcfRecord, AnnotationMode annotationMode) {
    Info info = vcfRecord.getInfo();
    switch (annotationMode) {
      case ADD -> {
        if (!reusableVcfInfoBuilder.isEmptyValue()) {
          info.append(infoId, reusableVcfInfoBuilder.build());
        }
      }
      case UPDATE -> {
        if (!reusableVcfInfoBuilder.isEmptyValue()) {
          info.put(infoId, reusableVcfInfoBuilder.build());
        } else {
          info.remove(infoId);
        }
      }
    }
    reusableVcfInfoBuilder.reset();
  }
}
