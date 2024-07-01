package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.Info;
import org.molgenis.vipannotate.format.vcf.VcfInfoSubfieldValueBuilder;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

public abstract class BaseVcfRecordAnnotator<T extends Annotation> implements VcfRecordAnnotator {
  // perf: reduce allocations and garbage collect pressure
  @Nullable private List<T> reusableAltAnnotations;
  @Nullable private VcfInfoSubfieldValueBuilder reusableVcfInfoBuilder;

  protected List<T> getAltAnnotationList() {
    if (reusableAltAnnotations == null) {
      reusableAltAnnotations = new ArrayList<>(1);
    } else {
      reusableAltAnnotations.clear();
    }
    return reusableAltAnnotations;
  }

  protected VcfInfoSubfieldValueBuilder getVcfInfoSubfieldBuilder() {
    if (reusableVcfInfoBuilder == null) {
      reusableVcfInfoBuilder = new VcfInfoSubfieldValueBuilder();
    } else {
      reusableVcfInfoBuilder.reset();
    }
    return reusableVcfInfoBuilder;
  }

  protected void writeInfoSubField(
      VcfRecord vcfRecord,
      String infoId,
      VcfInfoSubfieldValueBuilder infoValueBuilder,
      boolean isNewAnnotation) {
    Info info = vcfRecord.getInfo();
    if (isNewAnnotation) {
      if (!infoValueBuilder.isEmptyValue()) {
        info.append(infoId, infoValueBuilder.build());
      }
    } else {
      if (!infoValueBuilder.isEmptyValue()) {
        info.put(infoId, infoValueBuilder.build());
      } else {
        info.remove(infoId);
      }
    }
  }
}
