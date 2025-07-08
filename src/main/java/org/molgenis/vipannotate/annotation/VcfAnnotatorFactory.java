package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotatorFactory;
import org.molgenis.vipannotate.annotation.ncer.NcERAnnotatorFactory;
import org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotatorFactory;
import org.molgenis.vipannotate.annotation.remm.RemmAnnotatorFactory;
import org.molgenis.vipannotate.annotation.spliceai.SpliceAiAnnotatorFactory;
import org.molgenis.vipannotate.format.vcf.*;

@RequiredArgsConstructor
public class VcfAnnotatorFactory implements AutoCloseable {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  public VcfAnnotatorFactory() {
    this(new AnnotationBlobReaderFactory(), new VcfRecordAnnotationWriter());
  }

  public VcfAnnotator create(
      @Nullable Path inputVcf,
      Path annotationsZip,
      @Nullable Path outputVcf,
      @Nullable VcfType outputVcfType) {
    VcfParser vcfParser = VcfParserFactory.create(inputVcf);
    VcfRecordAnnotator vcfRecordAnnotator = createVcfRecordAnnotator(annotationsZip);
    VcfWriter vcfWriter = VcfWriterFactory.create(outputVcf, outputVcfType);
    return new VcfAnnotator(vcfParser, vcfRecordAnnotator, vcfWriter);
  }

  private VcfRecordAnnotatorAggregator createVcfRecordAnnotator(Path annotationsDir) {
    VcfRecordAnnotator vcfRecordAnnotatorGnomAd =
        new GnomAdAnnotatorFactory(annotationBlobReaderFactory, vcfRecordAnnotationWriter)
            .create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorNcER =
        new NcERAnnotatorFactory(annotationBlobReaderFactory, vcfRecordAnnotationWriter)
            .create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorPhyloP =
        new PhyloPAnnotatorFactory(annotationBlobReaderFactory, vcfRecordAnnotationWriter)
            .create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorRemm =
        new RemmAnnotatorFactory(annotationBlobReaderFactory, vcfRecordAnnotationWriter)
            .create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorSpliceAi =
        new SpliceAiAnnotatorFactory(annotationBlobReaderFactory, vcfRecordAnnotationWriter)
            .create(annotationsDir);
    return new VcfRecordAnnotatorAggregator(
        List.of(
            vcfRecordAnnotatorGnomAd,
            vcfRecordAnnotatorNcER,
            vcfRecordAnnotatorPhyloP,
            vcfRecordAnnotatorRemm,
            vcfRecordAnnotatorSpliceAi));
  }

  @Override
  public void close() {
    annotationBlobReaderFactory.close();
  }
}
