package org.molgenis.vipannotate.annotator.gnomad;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.molgenis.vipannotate.annotator.VcfRecordAnnotator;
import org.molgenis.vipannotate.db.exact.format.FuryFactory;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationData;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationDatasetDecoder;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationDatasetFactory;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationDatasetReader;
import org.molgenis.vipannotate.db.v2.*;
import org.molgenis.vipannotate.util.MappableZipFile;

@RequiredArgsConstructor
public class GnomAdAnnotatorFactory {
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

    org.molgenis.vipannotate.db.v2.AnnotationDbImpl<GnomAdShortVariantAnnotationData> annotationDb =
        new org.molgenis.vipannotate.db.v2.AnnotationDbImpl<>(
            annotationIndexReader, annotationDatasetReader);
    return new GnomAdAnnotator(annotationDb);
  }
}
