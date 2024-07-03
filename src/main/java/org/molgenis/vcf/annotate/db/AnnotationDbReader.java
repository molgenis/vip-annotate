package org.molgenis.vcf.annotate.db;

import java.io.*;
import org.apache.fury.Fury;
import org.apache.fury.io.FuryInputStream;
import org.molgenis.vcf.annotate.db.model.*;

public class AnnotationDbReader {

  public GenomeAnnotationDb readTranscriptDatabase(InputStream inputStream) throws IOException {
    Fury fury = FuryFactory.createFury();
    try (FuryInputStream furyInputStream = new FuryInputStream(inputStream)) {
      return (GenomeAnnotationDb) fury.deserialize(furyInputStream);
    }
  }
}
