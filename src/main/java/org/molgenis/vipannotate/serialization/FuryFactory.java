package org.molgenis.vipannotate.serialization;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.molgenis.vipannotate.annotation.*;

public class FuryFactory {
  // allow use of Fury in GraalVM native image, see
  // https://fury.apache.org/docs/guide/graalvm_guide/

  private static final Fury fury;

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
    fury.register(SequenceVariantAnnotationIndexSmall.class, true);
    fury.register(SequenceVariantAnnotationIndexBig.class, true);
    fury.register(SequenceVariantAnnotationIndex.class, true);
  }

  private FuryFactory() {}

  public static Fury createFury() {
    return fury;
  }
}
