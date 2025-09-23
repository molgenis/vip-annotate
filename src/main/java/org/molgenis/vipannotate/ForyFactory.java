package org.molgenis.vipannotate;

import org.apache.fory.Fory;
import org.apache.fory.config.Language;

public class ForyFactory {
  private static final Fory fory;

  static {
    fory =
        Fory.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(true)
            .registerGuavaTypes(false)
            .build();

    fory.register(SequenceVariantAnnotationIndexBig.class, true);
    fory.register(SequenceVariantAnnotationIndexSmall.class, true);
    fory.register(SequenceVariantAnnotationIndex.class, true);
    fory.ensureSerializersCompiled();
  }

  private ForyFactory() {}

  public static Fory createFory() {
    return fory;
  }
}
