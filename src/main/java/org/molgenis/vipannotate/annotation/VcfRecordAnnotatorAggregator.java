package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.vcf.VcfHeader;
import org.molgenis.vipannotate.vcf.VcfRecord;

@RequiredArgsConstructor
public class VcfRecordAnnotatorAggregator implements VcfRecordAnnotator {
  @NonNull private final List<VcfRecordAnnotator> vcfRecordAnnotators;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    for (VcfRecordAnnotator vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.updateHeader(vcfHeader);
    }
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    for (VcfRecordAnnotator vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.annotate(vcfRecord);
    }
  }

  @Override
  public void annotate(List<VcfRecord> vcfRecord) {
    for (VcfRecordAnnotator vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.annotate(vcfRecord);
    }
  }

  @Override
  public void close() {
    for (VcfRecordAnnotator vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.close();
    }
  }
}
