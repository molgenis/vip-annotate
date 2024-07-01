package org.molgenis.vipannotate.annotation.fathmmmkl;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.SNV;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.gnomad.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.serialization.BinarySerializer;

public class FathmmMklAnnotatorFactory
    extends SequenceVariantAnnotatorFactory<SequenceVariant, FathmmMklAnnotation> {
  public FathmmMklAnnotatorFactory(
      AnnotationBlobReaderFactory annotationBlobReaderFactory,
      PartitionResolver partitionResolver,
      BinarySerializer<AnnotationIndex<SequenceVariant>> indexSerializer) {
    super(annotationBlobReaderFactory, partitionResolver, indexSerializer);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    MappableZipFile zipFile = loadZipFile(annotationsDir, "fathmmmkl.zip");
    SequenceVariantAnnotationIndexReader<SequenceVariant> annotationIndexReader =
        createIndexReader(zipFile);

    AnnotationDatasetReader<FathmmMklAnnotation> annotationDatasetReader =
        new FathmmMklAnnotationDatasetReader(
            new FathmmMklAnnotationDatasetFactory(new FathmmMklAnnotationDatasetDecoder()),
            annotationBlobReaderFactory.create(zipFile, "score"));

    SequenceVariantAnnotationDb<SequenceVariant, FathmmMklAnnotation> annotationDb =
        buildAnnotationDb(annotationIndexReader, annotationDatasetReader, EnumSet.of(SNV));

    return new FathmmMklAnnotator(annotationDb);
  }
}
