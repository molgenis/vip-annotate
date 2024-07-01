package org.molgenis.vcf.annotate.db;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.molgenis.vcf.annotate.db.model.*;

public class FuryFactory {
  private FuryFactory() {}

  public static Fury createFury() {
    Fury fury = Fury.builder().withLanguage(Language.JAVA).requireClassRegistration(true).build();
    fury.register(AnnotationDb.class);
    fury.register(Cds.class);
    fury.register(Chromosome.class);
    fury.register(Exon.class);
    fury.register(Gene.class);
    fury.register(Gene.Type.class);
    fury.register(GenomeAnnotationDb.class);
    fury.register(Interval.class);
    fury.register(IntervalTree.class);
    fury.register(Sequence.class);
    fury.register(SequenceDb.class);
    fury.register(SequenceType.class);
    fury.register(Strand.class);
    fury.register(Transcript.class);
    fury.register(TranscriptCatalog.class);
    fury.register(TranscriptDb.class);
    fury.register(TranscriptRef.class);
    return fury;
  }
}
