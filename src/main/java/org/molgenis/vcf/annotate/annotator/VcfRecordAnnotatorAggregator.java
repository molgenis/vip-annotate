package org.molgenis.vcf.annotate.annotator;

import static java.util.Objects.requireNonNull;

import java.util.List;
import org.molgenis.vcf.annotate.vcf.VcfHeader;
import org.molgenis.vcf.annotate.vcf.VcfRecord;

public class VcfRecordAnnotatorAggregator implements VcfRecordAnnotator {
  private final List<VcfRecordAnnotator> vcfRecordAnnotators;

  public VcfRecordAnnotatorAggregator(List<VcfRecordAnnotator> vcfRecordAnnotators) {
    this.vcfRecordAnnotators = requireNonNull(vcfRecordAnnotators);
  }

  @Override
  public void close() throws Exception {
    for (VcfRecordAnnotator vcfRecordAnnotator : vcfRecordAnnotators) {
      vcfRecordAnnotator.close();
    }
  }

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
}
