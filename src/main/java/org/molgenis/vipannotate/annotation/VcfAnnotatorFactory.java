package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.fathmmmkl.FathmmMklAnnotatorFactory;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotatorFactory;
import org.molgenis.vipannotate.annotation.ncer.NcERAnnotatorFactory;
import org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotatorFactory;
import org.molgenis.vipannotate.annotation.remm.RemmAnnotatorFactory;
import org.molgenis.vipannotate.annotation.spliceai.SpliceAiAnnotatorFactory;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Output;

@RequiredArgsConstructor
public class VcfAnnotatorFactory {
  private final AnnotationVdbArchiveReaderFactory archiveReaderFactory;

  public VcfAnnotator create(
      Input inputVcf, Path annotationsZip, Output outputVcf, @Nullable VcfType outputVcfType) {
    VcfParser vcfParser = VcfParserFactory.create(inputVcf);
    VcfRecordAnnotator vcfRecordAnnotator = createVcfRecordAnnotator(annotationsZip);
    VcfWriter vcfWriter = VcfWriterFactory.create(outputVcf, outputVcfType);
    return new VcfAnnotator(vcfParser, vcfRecordAnnotator, vcfWriter);
  }

  private VcfRecordAnnotatorAggregator createVcfRecordAnnotator(Path annotationsDir) {
    PartitionResolver partitionResolver = new PartitionResolver();
    SequenceVariantAnnotationIndexDispatcherReaderFactory<SequenceVariant>
        indexDispatcherReaderFactory =
            SequenceVariantAnnotationIndexDispatcherReaderFactory.create();
    MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader =
        indexDispatcherReaderFactory.createReader();

    VcfRecordAnnotator vcfRecordAnnotatorFathmmMkl =
        new FathmmMklAnnotatorFactory(archiveReaderFactory, partitionResolver, indexReader)
            .create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorGnomAd =
        new GnomAdAnnotatorFactory(archiveReaderFactory, partitionResolver, indexReader)
            .create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorNcER =
        new NcERAnnotatorFactory(archiveReaderFactory, partitionResolver).create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorPhyloP =
        new PhyloPAnnotatorFactory(archiveReaderFactory, partitionResolver).create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorRemm =
        new RemmAnnotatorFactory(archiveReaderFactory, partitionResolver).create(annotationsDir);
    VcfRecordAnnotator vcfRecordAnnotatorSpliceAi =
        new SpliceAiAnnotatorFactory(archiveReaderFactory, partitionResolver, indexReader)
            .create(annotationsDir);
    return new VcfRecordAnnotatorAggregator(
        List.of(
            vcfRecordAnnotatorFathmmMkl,
            vcfRecordAnnotatorGnomAd,
            vcfRecordAnnotatorNcER,
            vcfRecordAnnotatorPhyloP,
            vcfRecordAnnotatorRemm,
            vcfRecordAnnotatorSpliceAi));
  }

  public static VcfAnnotatorFactory create() {
    return new VcfAnnotatorFactory(AnnotationVdbArchiveReaderFactory.create());
  }
}
