package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class VcfRecordAnnotatorAggregator implements VcfRecordAnnotatorOld {
  private final List<VcfRecordAnnotatorOld> vcfRecordAnnotators;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    for (VcfRecordAnnotatorOld vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.updateHeader(vcfHeader);
    }
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    for (VcfRecordAnnotatorOld vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.annotate(vcfRecord);
    }
  }

  @Override
  public void annotate(List<VcfRecord> vcfRecord) {
    for (VcfRecordAnnotatorOld vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.annotate(vcfRecord);
    }
  }

  @Override
  public void close() {
    ClosableUtils.closeAll(vcfRecordAnnotators);
  }
}
