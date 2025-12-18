package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.VcfHeaderAnnotator.HeaderUpdateResult;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class VcfAnnotationModule implements AutoCloseableNoThrow {
  private final VcfHeaderAnnotator vcfHeaderAnnotator;
  private final VcfRecordAnnotator vcfRecordAnnotator;

  public HeaderUpdateResult updateHeader(VcfHeader vcfHeader) {
    return vcfHeaderAnnotator.updateHeader(vcfHeader);
  }

  public void annotate(VcfRecord vcfRecord, AnnotationMode annotationMode) {
    vcfRecordAnnotator.annotate(vcfRecord, annotationMode);
  }

  public void annotate(List<VcfRecord> vcfRecordBatch, AnnotationMode annotationMode) {
    vcfRecordAnnotator.annotate(vcfRecordBatch, annotationMode);
  }

  @Override
  public void close() {
    ClosableUtils.close(vcfRecordAnnotator);
  }
}
