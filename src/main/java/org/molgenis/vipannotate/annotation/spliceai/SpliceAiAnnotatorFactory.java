package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.serialization.FuryFactory;

@RequiredArgsConstructor
public class SpliceAiAnnotatorFactory {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  public VcfRecordAnnotator create(Path annotationsDir) {
    SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> snvAnnotationDb =
        createAnnotationDb(annotationsDir, "spliceai_snv.zip");
    SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> indelAnnotationDb =
        createAnnotationDb(annotationsDir, "spliceai_indel.zip");
    return new SpliceAiAnnotator(snvAnnotationDb, indelAnnotationDb, vcfRecordAnnotationWriter);
  }

  private SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> createAnnotationDb(
      Path annotationsDir, String filename) {
    Path snpAnnotationsFile = annotationsDir.resolve(filename);
    if (Files.notExists(snpAnnotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(snpAnnotationsFile));
    }
    MappableZipFile mappableZipFile = MappableZipFile.fromFile(snpAnnotationsFile);

    AnnotationBlobReader annotationBlobReader =
        annotationBlobReaderFactory.create(mappableZipFile, "idx");

    Fury fury = FuryFactory.createFury();
    SequenceVariantAnnotationIndexReader annotationIndexReader =
        new SequenceVariantAnnotationIndexReader(annotationBlobReader, fury);

    SpliceAiAnnotationDatasetFactory spliceAiAnnotationDatasetFactory =
        new SpliceAiAnnotationDatasetFactory(new SpliceAiAnnotationDatasetDecoder());
    AnnotationDatasetReader<SpliceAiAnnotation> annotationDatasetReader =
        new SpliceAiAnnotationDatasetReader(
            spliceAiAnnotationDatasetFactory,
            annotationBlobReaderFactory.create(mappableZipFile, "gene_idx"),
            annotationBlobReaderFactory.create(mappableZipFile, "gene_ref"),
            annotationBlobReaderFactory.create(mappableZipFile, "ds_ag"),
            annotationBlobReaderFactory.create(mappableZipFile, "ds_al"),
            annotationBlobReaderFactory.create(mappableZipFile, "ds_dg"),
            annotationBlobReaderFactory.create(mappableZipFile, "ds_dl"),
            annotationBlobReaderFactory.create(mappableZipFile, "dp_ag"),
            annotationBlobReaderFactory.create(mappableZipFile, "dp_al"),
            annotationBlobReaderFactory.create(mappableZipFile, "dp_dg"),
            annotationBlobReaderFactory.create(mappableZipFile, "dp_dl"));

    return new SequenceVariantAnnotationDb<>(annotationIndexReader, annotationDatasetReader);
  }
}
