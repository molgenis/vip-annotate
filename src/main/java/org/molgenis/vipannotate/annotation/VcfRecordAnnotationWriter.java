package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.Info;
import org.molgenis.vipannotate.format.vcf.VcfInfoSubfieldValueBuilder;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

@RequiredArgsConstructor
public class VcfRecordAnnotationWriter<T extends Annotation> {
  private final String infoId;
  private final VcfInfoSubfieldValueBuilder reusableVcfInfoBuilder;

  public VcfRecordAnnotationWriter(String infoId) {
    this(infoId, new VcfInfoSubfieldValueBuilder());
  }

  public void appendAltAnnotation(@Nullable T altAnnotation) {
    if (altAnnotation == null) {
      reusableVcfInfoBuilder.appendValueMissing();
    } else {
      appendAnnotation(altAnnotation);
    }
  }

  private void appendAnnotation(Annotation annotation) {
    switch (annotation) {
      case ScalarAnnotation scalarAnnotation -> appendScalarAnnotation(scalarAnnotation);
      case CompositeAnnotation compositeAnnotation ->
          appendCompositeAnnotation(compositeAnnotation);
      default ->
          throw new IllegalArgumentException(
              "Unsupported annotation type: " + annotation.getClass());
    }
  }

  private void appendScalarAnnotation(ScalarAnnotation annotation) {
    reusableVcfInfoBuilder.startRawValue();
    appendRawScalarAnnotation(annotation);
    reusableVcfInfoBuilder.endRawValue();
  }

  private void appendRawScalarAnnotation(ScalarAnnotation annotation) {
    switch (annotation) {
      case ScalarAnnotation.DoubleAnnotation doubleAnnotation ->
          reusableVcfInfoBuilder.appendRaw(doubleAnnotation.getValue(), 3);

      case ScalarAnnotation.NullableDoubleAnnotation nullableDoubleAnnotation -> {
        if (nullableDoubleAnnotation.isNull()) {
          reusableVcfInfoBuilder.appendRawMissing();
        } else {
          reusableVcfInfoBuilder.appendRaw(nullableDoubleAnnotation.getValue(), 3);
        }
      }

      default ->
          throw new UnsupportedOperationException(
              "Unsupported scalar annotation type: " + annotation.getClass());
    }
  }

  private void appendCompositeAnnotation(CompositeAnnotation compositeAnnotation) {
    reusableVcfInfoBuilder.startRawValue();

    ScalarAnnotation[] annotations = compositeAnnotation.annotations();
    for (int i = 0; i < annotations.length; i++) {
      if (i > 0) {
        reusableVcfInfoBuilder.appendCompositeValueSeparator();
      }

      appendRawScalarAnnotation(annotations[i]);
    }

    reusableVcfInfoBuilder.endRawValue();
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
