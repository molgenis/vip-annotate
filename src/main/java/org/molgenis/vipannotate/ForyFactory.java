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
    fory.register(ObjImpl.class, true);
    fory.register(ObjConcrete.class, true);
    fory.ensureSerializersCompiled();
  }

  private ForyFactory() {}

  public static Fory createFory() {
    return fory;
  }

  public interface Obj {}

  public static class ObjImpl implements Obj {}

  public abstract static class ObjAbstract {}

  public static class ObjConcrete extends ObjAbstract {}
}
