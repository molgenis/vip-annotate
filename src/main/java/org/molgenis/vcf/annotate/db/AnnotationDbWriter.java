package org.molgenis.vcf.annotate.db;

import java.io.*;
import org.apache.fury.Fury;
import org.molgenis.vcf.annotate.db.model.*;

public class AnnotationDbWriter {

  public void writeTranscriptDatabase(GenomeAnnotationDb genomeAnnotationDb, File dbFile) {
    Fury fury = FuryFactory.createFury();

    try (FileOutputStream fileOutputStream = new FileOutputStream(dbFile)) {
      fury.serialize(fileOutputStream, genomeAnnotationDb);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
