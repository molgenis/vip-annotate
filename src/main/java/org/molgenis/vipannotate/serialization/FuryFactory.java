package org.molgenis.vipannotate.serialization;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.molgenis.vipannotate.annotation.AnnotationIndexImpl;
import org.molgenis.vipannotate.annotation.VariantAltAlleleAnnotationIndexBig;
import org.molgenis.vipannotate.annotation.VariantAltAlleleAnnotationIndexSmall;

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
    fury.register(VariantAltAlleleAnnotationIndexSmall.class, true);
    fury.register(VariantAltAlleleAnnotationIndexBig.class, true);
    fury.register(AnnotationIndexImpl.class, true);
  }

  private FuryFactory() {}

  public static Fury createFury() {
    return fury;
  }
}
