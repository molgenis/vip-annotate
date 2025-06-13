package org.molgenis.vipannotate.annotator;

import java.nio.file.Path;
import java.util.List;
import lombok.NonNull;
import org.molgenis.vipannotate.annotator.gnomad.GnomAdAnnotatorFactory;
import org.molgenis.vipannotate.db.v2.AnnotationBlobReaderFactory;
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
    // FIXME enable annotators
//    VcfRecordAnnotator vcfRecordAnnotatorPhyloP = PhyloPAnnotatorFactory.create(annotationsDir);
//    VcfRecordAnnotator vcfRecordAnnotatorNcER = NcERAnnotatorFactory.create(annotationsDir);
//    VcfRecordAnnotator vcfRecordAnnotatorRemm = RemmAnnotatorFactory.create(annotationsDir);
    return new VcfRecordAnnotatorAggregator(List.of(vcfRecordAnnotatorGnomAd /*,
            vcfRecordAnnotatorNcER,
            vcfRecordAnnotatorPhyloP,
            vcfRecordAnnotatorRemm*/));
  }

  @Override
  public void close() {
    annotationBlobReaderFactory.close();
  }
}
