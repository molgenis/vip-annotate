package org.molgenis.vcf.annotate.annotator;

import java.nio.file.Path;
import java.util.List;
import org.molgenis.vcf.annotate.db.chrpos.ncer.NcERAnnotatorFactory;
import org.molgenis.vcf.annotate.db.chrpos.phylop.PhyloPAnnotatorFactory;
import org.molgenis.vcf.annotate.db.chrpos.remm.RemmAnnotatorFactory;

public class VcfRecordAnnotatorAggregatorFactory {
  private VcfRecordAnnotatorAggregatorFactory() {}

  public VcfRecordAnnotatorAggregator create(Path annotationsDir) {
    // FIXME disabled effect annotator due to GraalVm issues
    //    VcfRecordAnnotator vcfRecordAnnotatorEffect =
    // VcfRecordAnnotatorEffect.create(annotationsZip);
    //    VcfRecordAnnotator vcfRecordAnnotatorGnomAd =
    // VcfRecordAnnotatorGnomAd.create(annotationsZip);
    VcfRecordAnnotator vcfRecordAnnotatorPhyloP = PhyloPAnnotatorFactory.create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorNcER = NcERAnnotatorFactory.create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorRemm = RemmAnnotatorFactory.create(annotationsDir);
    return new VcfRecordAnnotatorAggregator(
        List.of(vcfRecordAnnotatorNcER, vcfRecordAnnotatorPhyloP, vcfRecordAnnotatorRemm));
  }
}
