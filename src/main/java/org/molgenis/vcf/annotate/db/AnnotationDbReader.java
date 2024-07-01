package org.molgenis.vcf.annotate.db;

import java.io.*;
import org.apache.fury.Fury;
import org.apache.fury.io.FuryInputStream;
import org.molgenis.vcf.annotate.db.model.*;

public class AnnotationDbReader {

  public GenomeAnnotationDb readTranscriptDatabase(File dbFile) {
    Fury fury = FuryFactory.createFury();

    try (FuryInputStream furyInputStream = new FuryInputStream(new FileInputStream(dbFile))) {
      return (GenomeAnnotationDb) fury.deserialize(furyInputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
