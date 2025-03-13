package org.molgenis.vcf.annotate.db.effect;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.molgenis.vcf.annotate.db.effect.model.*;

public class FuryFactory {
  private static Fury fury;

  private FuryFactory() {}

  public static Fury createFury() {
    if (fury == null) {
      // TODO add .registerGuavaTypes(false) (requires database rebuild)
      fury = Fury.builder().withLanguage(Language.JAVA).requireClassRegistration(true).build();

      fury.register(AnnotationDb.class);
      fury.register(Cds.class);
      fury.register(Cds.Fragment.class);
      fury.register(Chromosome.class);
      fury.register(Exon.class);
      fury.register(Gene.class);
      fury.register(ClosedInterval.class);
      fury.register(IntervalTree.class);
      fury.register(Sequence.class);
      fury.register(SequenceDb.class);
      fury.register(SequenceType.class);
      fury.register(Strand.class);
      fury.register(Transcript.class);
      fury.register(Transcript.Type.class);
      fury.register(TranscriptDb.class);
    }
    return fury;
  }
}
