package org.molgenis.vipannotate.annotation.spliceai;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.*;

import java.nio.file.Path;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.serialization.BinarySerializer;

public class SpliceAiAnnotatorFactory
    extends SequenceVariantAnnotatorFactory<SequenceVariant, SpliceAiAnnotation> {
  public SpliceAiAnnotatorFactory(
      AnnotationBlobReaderFactory annotationBlobReaderFactory,
      PartitionResolver partitionResolver,
      BinarySerializer<AnnotationIndex<SequenceVariant>> indexSerializer) {
    super(annotationBlobReaderFactory, partitionResolver, indexSerializer);
  }

  @Override
  public VcfRecordAnnotator create(Path annotationsDir) {
    MappableZipFile zipFile = loadZipFile(annotationsDir, "spliceai.zip");
    SequenceVariantAnnotationIndexReader<SequenceVariant> annotationIndexReader =
        createIndexReader(zipFile);

    AnnotationDatasetReader<SpliceAiAnnotation> annotationDatasetReader =
        new SpliceAiAnnotationDatasetReader(
            new SpliceAiAnnotationDatasetFactory(new SpliceAiAnnotationDatasetDecoder()),
            annotationBlobReaderFactory.create(zipFile, "gene_idx"),
            annotationBlobReaderFactory.create(zipFile, "gene_ref"),
            annotationBlobReaderFactory.create(zipFile, "ds_ag"),
            annotationBlobReaderFactory.create(zipFile, "ds_al"),
            annotationBlobReaderFactory.create(zipFile, "ds_dg"),
            annotationBlobReaderFactory.create(zipFile, "ds_dl"),
            annotationBlobReaderFactory.create(zipFile, "dp_ag"),
            annotationBlobReaderFactory.create(zipFile, "dp_al"),
            annotationBlobReaderFactory.create(zipFile, "dp_dg"),
            annotationBlobReaderFactory.create(zipFile, "dp_dl"));

    SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> annotationDb =
        buildAnnotationDb(
            annotationIndexReader, annotationDatasetReader, EnumSet.of(SNV, INSERTION, DELETION));

    return new SpliceAiAnnotator(annotationDb);
  }
}
