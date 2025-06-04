package org.molgenis.vipannotate.annotator;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vcf.annotate.vcf.*;
import org.molgenis.vipannotate.annotator.gnomad.GnomAdAnnotatorFactory;
import org.molgenis.vipannotate.annotator.ncer.NcERAnnotatorFactory;
import org.molgenis.vipannotate.annotator.phylop.PhyloPAnnotatorFactory;
import org.molgenis.vipannotate.annotator.remm.RemmAnnotatorFactory;
import org.molgenis.vipannotate.vcf.*;

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
