package org.molgenis.vipannotate.db.chrpos.phylop;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.annotator.VcfRecordAnnotator;
import org.molgenis.vipannotate.db.chrpos.ContigPosAnnotationDb;
import org.molgenis.vipannotate.util.MappableZipFile;

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
