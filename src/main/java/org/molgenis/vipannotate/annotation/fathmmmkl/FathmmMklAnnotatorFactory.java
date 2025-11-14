package org.molgenis.vipannotate.annotation.fathmmmkl;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.SNV;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public class FathmmMklAnnotatorFactory
    extends SequenceVariantAnnotatorFactory<SequenceVariant, FathmmMklAnnotation> {
  public FathmmMklAnnotatorFactory(
      AnnotationVdbArchiveReaderFactory archiveReaderFactory,
      PartitionResolver partitionResolver,
      MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader) {
    super(archiveReaderFactory, partitionResolver, indexReader);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    AnnotationVdbArchiveReader archiveReader = createArchiveReader(annotationsDir, "fathmmmkl.zip");

    SequenceVariantAnnotationIndexReader<SequenceVariant> annotationIndexReader =
        createIndexReader(archiveReader);

    AnnotationDatasetReader<FathmmMklAnnotation> annotationDatasetReader =
        new FathmmMklAnnotationDatasetReader(
            new FathmmMklAnnotationDatasetFactory(new FathmmMklAnnotationDatasetDecoder()),
            new AnnotationBlobReader("score", archiveReader));

    SequenceVariantAnnotationDb<SequenceVariant, FathmmMklAnnotation> annotationDb =
        buildAnnotationDb(annotationIndexReader, annotationDatasetReader, EnumSet.of(SNV));

    return new FathmmMklAnnotator(annotationDb);
  }
}
