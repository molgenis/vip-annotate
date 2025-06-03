package org.molgenis.vcf.annotate.db.chrpos.remm;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vcf.annotate.annotator.VcfRecordAnnotator;
import org.molgenis.vcf.annotate.db.chrpos.ContigPosAnnotationDb;
import org.molgenis.vcf.annotate.util.MappableZipFile;

public class RemmAnnotatorFactory {
  private RemmAnnotatorFactory() {}

  public static VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("remm.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    ContigPosAnnotationDb contigPosAnnotationDb =
        new ContigPosAnnotationDb(
            MappableZipFile.fromFile(annotationsFile),
            new RemmAnnotationDecoder(),
            RemmAnnotationDecoder.NR_ANNOTATION_BYTES,
            RemmAnnotationDecoder.ANNOTATION_ID);
    return new RemmAnnotator(contigPosAnnotationDb);
  }
}
