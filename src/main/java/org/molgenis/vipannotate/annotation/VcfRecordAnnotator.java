package org.molgenis.vipannotate.annotation;

import java.util.List;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface VcfRecordAnnotator extends AutoCloseableNoThrow {
  void annotate(VcfRecord vcfRecord, AnnotationMode annotationMode);

  default void annotate(List<VcfRecord> vcfRecords, AnnotationMode annotationMode) {
    for (VcfRecord vcfRecord : vcfRecords) {
      annotate(vcfRecord, annotationMode);
    }
  }
}
