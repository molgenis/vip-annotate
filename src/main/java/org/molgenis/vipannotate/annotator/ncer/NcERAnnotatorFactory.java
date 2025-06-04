package org.molgenis.vipannotate.annotator.ncer;

import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vipannotate.annotator.VcfRecordAnnotator;
import org.molgenis.vipannotate.db.chrpos.ContigPosAnnotationDb;
import org.molgenis.vipannotate.util.MappableZipFile;

public class NcERAnnotatorFactory {
  private NcERAnnotatorFactory() {}

  public static VcfRecordAnnotator create(Path annotationsDir) {
    Path annotationsFile = annotationsDir.resolve("ncer.zip");
    if (Files.notExists(annotationsFile)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(annotationsFile));
    }

    ContigPosAnnotationDb contigPosAnnotationDb =
        new ContigPosAnnotationDb(
            MappableZipFile.fromFile(annotationsFile),
            new NcERAnnotationDecoder(),
            NcERAnnotationDecoder.NR_ANNOTATION_BYTES);
    return new NcERAnnotator(contigPosAnnotationDb);
  }
}
