package org.molgenis.vipannotate.annotation.phylop;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.annotation.ContigPosAnnotationDb;
import org.molgenis.vipannotate.annotation.VcfRecordAnnotator;
import org.molgenis.vipannotate.zip.MappableZipFile;

// FIXME refactor, see GnomAdAnnotatorFactory
public class PhyloPAnnotatorFactory {
  private PhyloPAnnotatorFactory() {}

  public static VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("phylop.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    ContigPosAnnotationDb phyloPAnnotationDb =
        new ContigPosAnnotationDb(
            MappableZipFile.fromFile(annotationsFile),
            new PhyloPAnnotationDecoder(),
            PhyloPAnnotationDecoder.NR_ANNOTATION_BYTES);
    return new PhyloPAnnotator(phyloPAnnotationDb);
  }
}
