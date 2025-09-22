package org.molgenis.vipannotate;

import java.util.Objects;
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
    fory.register(IntWrapperRecord.class, true);
    fory.register(IntWrapperClass.class, true);
    fory.ensureSerializersCompiled();
  }

  private ForyFactory() {}

  public static Fory createFory() {
    return fory;
  }

  public record IntWrapperRecord(int integer) {}

  public static final class IntWrapperClass {
    private final int integer;

    public IntWrapperClass(int integer) {
      this.integer = integer;
    }

    public int integer() {
      return integer;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      var that = (IntWrapperClass) obj;
      return this.integer == that.integer;
    }

    @Override
    public int hashCode() {
      return Objects.hash(integer);
    }

    @Override
    public String toString() {
      return "IntWrapperClass[" + "integer=" + integer + ']';
    }
  }
}
