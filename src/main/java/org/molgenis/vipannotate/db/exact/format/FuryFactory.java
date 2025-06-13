package org.molgenis.vipannotate.db.exact.format;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.molgenis.vipannotate.db.exact.formatv2.VariantAltAlleleAnnotationIndex;
import org.molgenis.vipannotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexBig;
import org.molgenis.vipannotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexSmall;
import org.molgenis.vipannotate.db.v2.AnnotationIndexImpl;

public class FuryFactory {
  // allow use of Fury in GraalVM native image, see
  // https://fury.apache.org/docs/guide/graalvm_guide/

  // TODO check if can be private
  static Fury fury;

  static {
    fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(true)
            .registerGuavaTypes(false)
            .build();

    // register and generate serializer code.
    // order matters
    fury.registerSerializer(SortedIntArrayWrapper.class, SortedIntArrayWrapperSerializer.class);
    //    fury.register(AnnotationData.class, true);
    //    fury.register(AnnotationDb.class, true);
    //    fury.register(VariantAltAlleleAnnotationData.class, true);
    fury.register(VariantAltAlleleAnnotationIndexSmall.class, true);
    fury.register(VariantAltAlleleAnnotationIndexBig.class, true);
    fury.register(VariantAltAlleleAnnotationIndex.class, true);

    // for gnomad shortvariant
    fury.register(AnnotationIndexImpl.class, true);
  }

  private FuryFactory() {}

  public static Fury createFury() {
    return fury;
  }
}
