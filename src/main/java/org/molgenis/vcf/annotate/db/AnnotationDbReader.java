package org.molgenis.vcf.annotate.db;

import java.io.*;
import org.apache.fury.Fury;
import org.apache.fury.io.FuryInputStream;
import org.molgenis.vcf.annotate.db.model.*;
import org.molgenis.vcf.annotate.db.utils.AnnotationDbImpl;

/**
 * @see AnnotationDbImpl
 */
@Deprecated
public class AnnotationDbReader {
  public GenomeAnnotationDb readTranscriptDatabase(InputStream inputStream) throws IOException {
    // TODO use buffer, see AnnotationDbImpl
    Fury fury = FuryFactory.createFury();

    try (FuryInputStream furyInputStream = new FuryInputStream(inputStream)) {
      return (GenomeAnnotationDb) fury.deserialize(furyInputStream);
    }
  }
}
