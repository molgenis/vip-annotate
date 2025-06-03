package org.molgenis.vcf.annotate.annotator;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.List;
import org.molgenis.vcf.annotate.db.chrpos.ncer.VcfRecordAnnotatorNcER;
import org.molgenis.vcf.annotate.db.chrpos.phylop.VcfRecordAnnotatorPhyloP;
import org.molgenis.vcf.annotate.db.chrpos.remm.VcfRecordAnnotatorRemm;
import org.molgenis.vcf.annotate.util.MappableZipFile;
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

  public VcfRecordAnnotatorAggregator create(Path annotationsZip) {
    MappableZipFile zipFile = MappableZipFile.fromFile(annotationsZip);

    // FIXME disabled effect annotator due to GraalVm issues
    //    VcfRecordAnnotator vcfRecordAnnotatorEffect =
    // VcfRecordAnnotatorEffect.create(annotationsZip);
    //    VcfRecordAnnotator vcfRecordAnnotatorGnomAd =
    // VcfRecordAnnotatorGnomAd.create(annotationsZip);
    VcfRecordAnnotator vcfRecordAnnotatorPhyloP = VcfRecordAnnotatorPhyloP.create(zipFile);
    VcfRecordAnnotator vcfRecordAnnotatorNcER = VcfRecordAnnotatorNcER.create(zipFile);
    VcfRecordAnnotator vcfRecordAnnotatorRemm = VcfRecordAnnotatorRemm.create(zipFile);
    return new VcfRecordAnnotatorAggregator(
        List.of(vcfRecordAnnotatorNcER, vcfRecordAnnotatorPhyloP, vcfRecordAnnotatorRemm));
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
