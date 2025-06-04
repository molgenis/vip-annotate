package org.molgenis.vipannotate.annotator;

import java.util.List;
import org.molgenis.vipannotate.vcf.VcfHeader;
import org.molgenis.vipannotate.vcf.VcfRecord;

public interface VcfRecordAnnotator extends AutoCloseable {
  void updateHeader(VcfHeader vcfHeader);

  void annotate(VcfRecord vcfRecord);

  default void annotate(List<VcfRecord> vcfRecords) {
    for (VcfRecord vcfRecord : vcfRecords) {
      annotate(vcfRecord);
    }
  }
}
