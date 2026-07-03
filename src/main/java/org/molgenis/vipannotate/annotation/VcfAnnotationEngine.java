package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.VcfHeaderAnnotator.HeaderUpdateResult;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

public class VcfAnnotationEngine implements AutoCloseableNoThrow {
  private final List<VcfAnnotationModule> annotationModules;
  @Nullable private List<@Nullable AnnotationMode> annotationModes;

  public VcfAnnotationEngine() {
    annotationModules = new ArrayList<>();
    annotationModes = null;
  }

  public void registerModule(VcfAnnotationModule annotationModule) {
    annotationModules.add(annotationModule);
  }

  public void updateHeader(VcfHeader vcfHeader) {
    annotationModes = new ArrayList<>(annotationModules.size());
    annotationModules.forEach(
        annotationModule -> {
          HeaderUpdateResult updateResult = annotationModule.updateHeader(vcfHeader);
          annotationModes.add(
              switch (updateResult) {
                case ADDED -> AnnotationMode.ADD;
                case EXISTS_DIFF -> AnnotationMode.UPDATE;
                case EXISTS_SAME -> null;
              });
        });
  }

  public void annotate(VcfRecord vcfRecord) {
    if (annotationModes == null) {
      throw new IllegalArgumentException("updateHeader must be called before annotate");
    }
    for (int i = 0, size = annotationModes.size(); i < size; i++) {
      AnnotationMode annotationMode = annotationModes.get(i);
      if (annotationMode != null) {
        annotationModules.get(i).annotate(vcfRecord, annotationMode);
      }
    }
  }

  public void annotate(List<VcfRecord> vcfRecordBatch) {
    if (annotationModes == null) {
      throw new IllegalArgumentException("updateHeader must be called before annotate");
    }
    for (int i = 0, size = annotationModes.size(); i < size; i++) {
      annotationModules.get(i).annotate(vcfRecordBatch, annotationModes.get(i));
    }
  }

  @Override
  public void close() {
    ClosableUtils.closeAll(annotationModules);
  }
}
