package org.molgenis.vipannotate.annotation.remm;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.annotation.ContigPosAnnotationDb;
import org.molgenis.vipannotate.annotation.VcfRecordAnnotator;
import org.molgenis.vipannotate.zip.MappableZipFile;

// FIXME refactor, see GnomAdAnnotatorFactory
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
            RemmAnnotationDecoder.NR_ANNOTATION_BYTES);
    return new RemmAnnotator(contigPosAnnotationDb);
  }
}
