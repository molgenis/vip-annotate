package org.molgenis.vcf.annotate.annotator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vcf.annotate.db.chrpos.ncer.VcfRecordAnnotatorNcER;
import org.molgenis.vcf.annotate.db.chrpos.phylop.VcfRecordAnnotatorPhyloP;
import org.molgenis.vcf.annotate.db.chrpos.remm.VcfRecordAnnotatorRemm;
import org.molgenis.vcf.annotate.util.CloseIgnoringInputStream;
import org.molgenis.vcf.annotate.util.CloseIgnoringOutputStream;
import org.molgenis.vcf.annotate.util.MappableZipFile;
import org.molgenis.vcf.annotate.vcf.VcfReader;
import org.molgenis.vcf.annotate.vcf.VcfWriter;

// FIXME annotation database per annotator
public class VcfAnnotatorCreator {
  private VcfAnnotatorCreator() {}

  public static VcfAnnotator create(Path inputVcf, Path annotationsZip, Path outputVcf)
      throws IOException {
    VcfReader vcfReader = VcfReader.create(createInputStream(inputVcf));

    VcfRecordAnnotatorAggregator vcfRecordAnnotatorAggregator =
        createRecordAnnotators(annotationsZip);
    VcfWriter vcfWriter = VcfWriter.create(createOutputStream(outputVcf));

    return new VcfAnnotator(vcfReader, vcfRecordAnnotatorAggregator, vcfWriter);
  }

  private static VcfRecordAnnotatorAggregator createRecordAnnotators(Path annotationsZip) {
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

  private static InputStream createInputStream(Path inputVcfPath) throws IOException {
    InputStream inputStream;
    if (inputVcfPath != null) {
      inputStream = Files.newInputStream(inputVcfPath);
    } else {
      inputStream = new CloseIgnoringInputStream(System.in);
    }
    return inputStream;
  }

  private static OutputStream createOutputStream(Path outputVcfPath) throws IOException {
    OutputStream outputStream;
    if (outputVcfPath != null) {
      outputStream = Files.newOutputStream(outputVcfPath);
    } else {
      outputStream = new CloseIgnoringOutputStream(System.out);
    }
    return outputStream;
  }
}
