package org.molgenis.vipannotate.annotation.spliceai;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.*;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public class SpliceAiAnnotatorFactory
    extends SequenceVariantAnnotatorFactory<SequenceVariant, SpliceAiAnnotation> {
  public SpliceAiAnnotatorFactory(
      AnnotationVdbArchiveReaderFactory archiveReaderFactory,
      PartitionResolver partitionResolver,
      MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader) {
    super(archiveReaderFactory, partitionResolver, indexReader);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    AnnotationVdbArchiveReader archiveReader = createArchiveReader(annotationsDir, "spliceai.zip");

    SequenceVariantAnnotationIndexReader<SequenceVariant> annotationIndexReader =
        createIndexReader(archiveReader);

    AnnotationDatasetReader<SpliceAiAnnotation> annotationDatasetReader =
        new SpliceAiAnnotationDatasetReader(
            new SpliceAiAnnotationDatasetFactory(new SpliceAiAnnotationDatasetDecoder()),
            new AnnotationBlobReader("gene_idx", archiveReader),
            new AnnotationBlobReader("gene_ref", archiveReader),
            new AnnotationBlobReader("ds_ag", archiveReader),
            new AnnotationBlobReader("ds_al", archiveReader),
            new AnnotationBlobReader("ds_dg", archiveReader),
            new AnnotationBlobReader("ds_dl", archiveReader),
            new AnnotationBlobReader("dp_ag", archiveReader),
            new AnnotationBlobReader("dp_al", archiveReader),
            new AnnotationBlobReader("dp_dg", archiveReader),
            new AnnotationBlobReader("dp_dl", archiveReader));

    SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> annotationDb =
        buildAnnotationDb(
            annotationIndexReader, annotationDatasetReader, EnumSet.of(SNV, INSERTION, DELETION));

    return new SpliceAiAnnotator(annotationDb);
  }
}
