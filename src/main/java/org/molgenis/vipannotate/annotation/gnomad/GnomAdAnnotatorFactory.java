package org.molgenis.vipannotate.annotation.gnomad;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.serialization.BinarySerializer;

public class GnomAdAnnotatorFactory
    extends SequenceVariantAnnotatorFactory<SequenceVariant, GnomAdAnnotation> {
  public GnomAdAnnotatorFactory(
      AnnotationBlobReaderFactory annotationBlobReaderFactory,
      PartitionResolver partitionResolver,
      BinarySerializer<AnnotationIndex<SequenceVariant>> indexSerializer) {
    super(annotationBlobReaderFactory, partitionResolver, indexSerializer);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    MappableZipFile zipFile = loadZipFile(annotationsDir, "gnomad.zip");
    SequenceVariantAnnotationIndexReader<SequenceVariant> annotationIndexReader =
        createIndexReader(zipFile);

    AnnotationDatasetReader<GnomAdAnnotation> annotationDatasetReader =
        new GnomAdAnnotationDatasetReader(
            new GnomAdAnnotationDatasetFactory(new GnomAdAnnotationDatasetDecoder()),
            annotationBlobReaderFactory.create(zipFile, "src"),
            annotationBlobReaderFactory.create(zipFile, "af"),
            annotationBlobReaderFactory.create(zipFile, "faf95"),
            annotationBlobReaderFactory.create(zipFile, "faf99"),
            annotationBlobReaderFactory.create(zipFile, "hn"),
            annotationBlobReaderFactory.create(zipFile, "filters"),
            annotationBlobReaderFactory.create(zipFile, "cov"));

    @SuppressWarnings("DataFlowIssue")
    SequenceVariantAnnotationDb<SequenceVariant, GnomAdAnnotation> annotationDb =
        buildAnnotationDb(
            annotationIndexReader,
            annotationDatasetReader,
            EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER)));

    return new GnomAdAnnotator(annotationDb);
  }
}
