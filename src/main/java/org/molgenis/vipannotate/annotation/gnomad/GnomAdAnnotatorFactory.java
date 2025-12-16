package org.molgenis.vipannotate.annotation.gnomad;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReaderFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public class GnomAdAnnotatorFactory
    extends SequenceVariantAnnotatorFactory<SequenceVariant, GnomAdAnnotation> {
  public GnomAdAnnotatorFactory(
      PartitionedVdbArchiveReaderFactory archiveReaderFactory,
      PartitionResolver partitionResolver,
      MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader) {
    super(archiveReaderFactory, partitionResolver, indexReader);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    PartitionedVdbArchiveReader archiveReader = createArchiveReader(annotationsDir, "gnomad.zip");

    SequenceVariantAnnotationIndexReader<SequenceVariant> annotationIndexReader =
        createIndexReader(archiveReader);

    AnnotationDatasetReader<GnomAdAnnotation> annotationDatasetReader =
        new GnomAdAnnotationDatasetReader(
            new GnomAdAnnotationDatasetFactory(new GnomAdAnnotationDatasetDecoder()),
            new AnnotationBlobReader("src", archiveReader),
            new AnnotationBlobReader("af", archiveReader),
            new AnnotationBlobReader("faf95", archiveReader),
            new AnnotationBlobReader("faf99", archiveReader),
            new AnnotationBlobReader("hn", archiveReader),
            new AnnotationBlobReader("filters", archiveReader),
            new AnnotationBlobReader("cov", archiveReader));

    @SuppressWarnings("DataFlowIssue")
    SequenceVariantAnnotationDb<SequenceVariant, GnomAdAnnotation> annotationDb =
        buildAnnotationDb(
            annotationIndexReader,
            annotationDatasetReader,
            EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER)));

    return new GnomAdAnnotator(annotationDb);
  }
}
