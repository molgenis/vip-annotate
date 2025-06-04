package org.molgenis.vcf.annotate.annotator;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vcf.annotate.annotator.gnomad.GnomAdAnnotatorFactory;
import org.molgenis.vcf.annotate.db.chrpos.ncer.NcERAnnotatorFactory;
import org.molgenis.vcf.annotate.db.chrpos.phylop.PhyloPAnnotatorFactory;
import org.molgenis.vcf.annotate.db.chrpos.remm.RemmAnnotatorFactory;
import org.molgenis.vcf.annotate.vcf.*;

public class VcfAnnotatorFactory {
  private VcfAnnotatorFactory() {}

  public static VcfAnnotator create(
      Path inputVcf, Path annotationsZip, Path outputVcf, VcfType outputVcfType)
      throws IOException {
    VcfReader vcfReader = VcfReaderFactory.create(inputVcf);
    VcfRecordAnnotator vcfRecordAnnotator = createVcfRecordAnnotator(annotationsZip);
    VcfWriter vcfWriter = VcfWriterFactory.create(outputVcf, outputVcfType);
    return new VcfAnnotator(vcfReader, vcfRecordAnnotator, vcfWriter);
  }

  private static VcfRecordAnnotatorAggregator createVcfRecordAnnotator(Path annotationsDir) {
    // FIXME disabled effect annotator due to GraalVm issues
    //    VcfRecordAnnotator vcfRecordAnnotatorEffect =
    // VcfRecordAnnotatorEffect.create(annotationsZip);
    VcfRecordAnnotator vcfRecordAnnotatorGnomAd = GnomAdAnnotatorFactory.create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorPhyloP = PhyloPAnnotatorFactory.create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorNcER = NcERAnnotatorFactory.create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorRemm = RemmAnnotatorFactory.create(annotationsDir);
    return new VcfRecordAnnotatorAggregator(
        List.of(vcfRecordAnnotatorGnomAd, vcfRecordAnnotatorNcER, vcfRecordAnnotatorPhyloP, vcfRecordAnnotatorRemm));
  }
}
