package org.molgenis.vcf.annotate.annotator.gnomad;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vcf.annotate.annotator.VcfRecordAnnotator;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationDbImpl;
import org.molgenis.vcf.annotate.db.gnomad.GnomAdShortVariantAnnotation;
import org.molgenis.vcf.annotate.db.gnomad.GnomAdShortVariantAnnotationCodec;
import org.molgenis.vcf.annotate.util.MappableZipFile;

public class GnomAdAnnotatorFactory {
  private GnomAdAnnotatorFactory() {}

  public static VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("gnomad_and_effect.zip");
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
