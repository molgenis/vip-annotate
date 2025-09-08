package org.molgenis.vipannotate.serialization;

import org.apache.fory.Fory;
import org.apache.fory.config.Language;
import org.molgenis.vipannotate.annotation.*;

public class ForyFactory {
  // allow use of Fory in GraalVM native image, see
  // https://fory.apache.org/docs/guide/graalvm_guide/

  private static final Fory fory;

  static {
    fory =
        Fory.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(true)
            .registerGuavaTypes(false)
            .build();

    // register and generate serializer code.
    // order matters
    fory.registerSerializer(SortedIntArrayWrapper.class, SortedIntArrayWrapperSerializer.class);
    fory.register(SequenceVariantAnnotationIndexSmall.class, true);
    fory.register(SequenceVariantAnnotationIndexBig.class, true);
    fory.register(SequenceVariantAnnotationIndex.class, true);
  }

  private ForyFactory() {}

  public static Fory createFory() {
    return fory;
  }
}
