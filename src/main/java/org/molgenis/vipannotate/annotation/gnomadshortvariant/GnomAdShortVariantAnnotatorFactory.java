package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.serialization.FuryFactory;
import org.molgenis.vipannotate.zip.MappableZipFile;

@RequiredArgsConstructor
public class GnomAdShortVariantAnnotatorFactory {
  @NonNull private final AnnotationBlobReaderFactory annotationBlobReaderFactory;

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

    GnomAdShortVariantAnnotationDatasetFactory gnomAdShortVariantAnnotationDatasetFactory =
        new GnomAdShortVariantAnnotationDatasetFactory(
            new GnomAdShortVariantAnnotationDatasetDecoder());
    AnnotationDatasetReader<GnomAdShortVariantAnnotationData> annotationDatasetReader =
        new GnomAdShortVariantAnnotationDatasetReader(
            gnomAdShortVariantAnnotationDatasetFactory,
            annotationBlobReaderFactory.create(mappableZipFile, "src"),
            annotationBlobReaderFactory.create(mappableZipFile, "af"),
            annotationBlobReaderFactory.create(mappableZipFile, "faf95"),
            annotationBlobReaderFactory.create(mappableZipFile, "faf99"),
            annotationBlobReaderFactory.create(mappableZipFile, "hn"),
            annotationBlobReaderFactory.create(mappableZipFile, "filters"),
            annotationBlobReaderFactory.create(mappableZipFile, "cov"));

    AnnotationDbImpl<GnomAdShortVariantAnnotationData> annotationDb =
        new AnnotationDbImpl<>(annotationIndexReader, annotationDatasetReader);
    return new GnomAdShortVariantAnnotator(annotationDb);
  }
}
