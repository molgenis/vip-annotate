package org.molgenis.vipannotate.annotation.gnomad;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.serialization.FuryFactory;

@RequiredArgsConstructor
public class GnomAdAnnotatorFactory {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("gnomad.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }
    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    AnnotationBlobReader annotationBlobReader =
        annotationBlobReaderFactory.create(mappableZipFile, "idx");

    Fury fury = FuryFactory.createFury();
    AnnotationIndexReader annotationIndexReader =
        new AnnotationIndexReader(annotationBlobReader, fury);

    GnomAdAnnotationDatasetFactory gnomAdAnnotationDatasetFactory =
        new GnomAdAnnotationDatasetFactory(new GnomAdAnnotationDatasetDecoder());
    AnnotationDatasetReader<GnomAdAnnotation> annotationDatasetReader =
        new GnomAdAnnotationDatasetReader(
            gnomAdAnnotationDatasetFactory,
            annotationBlobReaderFactory.create(mappableZipFile, "src"),
            annotationBlobReaderFactory.create(mappableZipFile, "af"),
            annotationBlobReaderFactory.create(mappableZipFile, "faf95"),
            annotationBlobReaderFactory.create(mappableZipFile, "faf99"),
            annotationBlobReaderFactory.create(mappableZipFile, "hn"),
            annotationBlobReaderFactory.create(mappableZipFile, "filters"),
            annotationBlobReaderFactory.create(mappableZipFile, "cov"));

    SequenceVariantAnnotationDb<GnomAdAnnotation> annotationDb =
        new SequenceVariantAnnotationDb<>(annotationIndexReader, annotationDatasetReader);
    // TODO only one VcfRecordAnnotationWriter globally
    return new GnomAdAnnotator(annotationDb, new VcfRecordAnnotationWriter());
  }
}
