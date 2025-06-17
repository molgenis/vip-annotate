package org.molgenis.vipannotate.annotation;

import java.util.List;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

public interface VcfRecordAnnotator extends AutoCloseable {
  void updateHeader(VcfHeader vcfHeader);

  void annotate(VcfRecord vcfRecord);

  default void annotate(List<VcfRecord> vcfRecords) {
    for (VcfRecord vcfRecord : vcfRecords) {
      annotate(vcfRecord);
    }
  }

  void close();
}
