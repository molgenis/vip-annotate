package org.molgenis.vipannotate;

import java.math.BigInteger;
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

    // issue #1 : BigInteger[] needs to be registered, but only for GraalVm
    if (GraalVm.isGraalVmRuntime()) {
      fory.register(BigInteger[].class);
    }
    fory.register(SequenceVariantAnnotationIndexBig.class, true);
    fory.register(SequenceVariantAnnotationIndexSmall.class, true);
    fory.register(SequenceVariantAnnotationIndex.class, true);
    fory.ensureSerializersCompiled();
  }

  private ForyFactory() {}

  public static Fory createFory() {
    return fory;
  }

  public static class GraalVm {
    private static final String GRAALVM_IMAGE_CODE_KEY = "org.graalvm.nativeimage.imagecode";
    private static final String GRAALVM_IMAGE_RUNTIME = "runtime";

    /** Returns true if the current process is executing at image runtime. */
    public static boolean isGraalVmRuntime() {
      return GRAALVM_IMAGE_RUNTIME.equals(System.getProperty(GRAALVM_IMAGE_CODE_KEY));
    }
  }
}
