package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.format.vcf.VcfHeader;

public interface VcfHeaderAnnotator {
  enum HeaderUpdateResult {
    ADDED,
    EXISTS_DIFF,
    EXISTS_SAME
  }

  HeaderUpdateResult updateHeader(VcfHeader vcfHeader);
}
