package org.molgenis.vipannotate.annotator.gnomad;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.annotator.VcfRecordAnnotator;
import org.molgenis.vipannotate.db.exact.format.AnnotationDbImpl;
import org.molgenis.vipannotate.db.gnomad.GnomAdShortVariantAnnotation;
import org.molgenis.vipannotate.db.gnomad.GnomAdShortVariantAnnotationCodec;
import org.molgenis.vipannotate.util.MappableZipFile;

public class GnomAdAnnotatorFactory {
  private GnomAdAnnotatorFactory() {}

  public static VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("gnomad.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    GnomAdShortVariantAnnotationCodec gnomAdShortVariantAnnotationCodec =
        new GnomAdShortVariantAnnotationCodec();
    AnnotationDbImpl<GnomAdShortVariantAnnotation> annotationDb =
        new AnnotationDbImpl<>(
            MappableZipFile.fromFile(annotationsFile), gnomAdShortVariantAnnotationCodec);
    return new GnomAdAnnotator(annotationDb);
  }
}
