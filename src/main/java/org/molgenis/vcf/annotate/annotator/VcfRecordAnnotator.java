package org.molgenis.vcf.annotate.annotator;

import java.util.List;
import org.molgenis.vcf.annotate.vcf.VcfHeader;
import org.molgenis.vcf.annotate.vcf.VcfRecord;

public interface VcfRecordAnnotator extends AutoCloseable {
  void updateHeader(VcfHeader vcfHeader);

  void annotate(VcfRecord vcfRecord);

  default void annotate(List<VcfRecord> vcfRecords) {
    for (VcfRecord vcfRecord : vcfRecords) {
      annotate(vcfRecord);
    }
  }
}
