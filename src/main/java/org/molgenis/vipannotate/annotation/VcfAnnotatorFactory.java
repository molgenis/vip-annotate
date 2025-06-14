package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.List;
import lombok.NonNull;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotatorFactory;
import org.molgenis.vipannotate.annotation.ncer.NcERAnnotatorFactory;
import org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotatorFactory;
import org.molgenis.vipannotate.annotation.remm.RemmAnnotatorFactory;
import org.molgenis.vipannotate.vcf.*;

public class VcfAnnotatorFactory implements AutoCloseable {
  public final AnnotationBlobReaderFactory annotationBlobReaderFactory;

  public VcfAnnotatorFactory() {
    this(new AnnotationBlobReaderFactory());
  }

  VcfAnnotatorFactory(@NonNull AnnotationBlobReaderFactory annotationBlobReaderFactory) {
    this.annotationBlobReaderFactory = annotationBlobReaderFactory;
  }

  public VcfAnnotator create(
      Path inputVcf, Path annotationsZip, Path outputVcf, VcfType outputVcfType) {
    VcfReader vcfReader = VcfReaderFactory.create(inputVcf);
    VcfRecordAnnotator vcfRecordAnnotator = createVcfRecordAnnotator(annotationsZip);
    VcfWriter vcfWriter = VcfWriterFactory.create(outputVcf, outputVcfType);
    return new VcfAnnotator(vcfReader, vcfRecordAnnotator, vcfWriter);
  }

  private VcfRecordAnnotatorAggregator createVcfRecordAnnotator(Path annotationsDir) {
    VcfRecordAnnotator vcfRecordAnnotatorGnomAd =
        new GnomAdAnnotatorFactory(annotationBlobReaderFactory).create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorNcER =
        new NcERAnnotatorFactory(annotationBlobReaderFactory).create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorPhyloP =
        new PhyloPAnnotatorFactory(annotationBlobReaderFactory).create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorRemm =
        new RemmAnnotatorFactory(annotationBlobReaderFactory).create(annotationsDir);
    return new VcfRecordAnnotatorAggregator(
        List.of(
            vcfRecordAnnotatorGnomAd,
            vcfRecordAnnotatorNcER,
            vcfRecordAnnotatorPhyloP,
            vcfRecordAnnotatorRemm));
  }

  @Override
  public void close() {
    annotationBlobReaderFactory.close();
  }
}
