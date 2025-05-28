package org.molgenis.vcf.annotate.annotator;

import org.molgenis.vcf.annotate.vcf.VcfHeader;
import org.molgenis.vcf.annotate.vcf.VcfRecord;

public interface VcfRecordAnnotator extends AutoCloseable {
  void updateHeader(VcfHeader vcfHeader);

  void annotate(VcfRecord vcfRecord);
}
