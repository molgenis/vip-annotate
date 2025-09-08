package org.molgenis.vipannotate.annotation.fathmmmkl;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.apache.fory.Fory;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.gnomad.*;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.serialization.ForyFactory;

@RequiredArgsConstructor
public class FathmmMklAnnotatorFactory {
  private final AnnotationBlobReaderFactory annotationBlobReaderFactory;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  public VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("fathmmmkl.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }
    MappableZipFile mappableZipFile = MappableZipFile.fromFile(annotationsFile);

    AnnotationBlobReader annotationBlobReader =
        annotationBlobReaderFactory.create(mappableZipFile, "idx");

    Fory fory = ForyFactory.createFory();
    SequenceVariantAnnotationIndexReader annotationIndexReader =
        new SequenceVariantAnnotationIndexReader(annotationBlobReader, fory);

    FathmmMklAnnotationDatasetFactory annotationDatasetFactory =
        new FathmmMklAnnotationDatasetFactory(new FathmmMklAnnotationDatasetDecoder());
    AnnotationDatasetReader<FathmmMklAnnotation> annotationDatasetReader =
        new FathmmMklAnnotationDatasetReader(
            annotationDatasetFactory, annotationBlobReaderFactory.create(mappableZipFile, "score"));

    SequenceVariantAnnotationDb<SequenceVariant, FathmmMklAnnotation> annotationDb =
        new SequenceVariantAnnotationDb<>(annotationIndexReader, annotationDatasetReader);
    return new FathmmMklAnnotator(annotationDb, vcfRecordAnnotationWriter);
  }
}
