package org.molgenis.vcf.annotate;

import org.apache.fury.*;
import org.apache.fury.config.*;
import org.apache.fury.logging.LoggerFactory;

public class FurySizeTest {
  public static class FloatPrimitive {
    private float x = Float.MAX_VALUE; // 8 bytes
  }

  public static class FloatObject {
    private Float x = Float.MAX_VALUE; // 9 bytes
  }

  public static class FloatObjectNull {
    private Float x = null; // 5 bytes
  }

  public static class BytePrimitive {
    private byte x = Byte.MAX_VALUE;
  }

  public static class ShortPrimitive {
    private short x = Short.MAX_VALUE;
  }

  public static class IntPrimitive {
    private int x = Integer.MAX_VALUE;
  }

  public enum EnumPrimitive {
    A,
    B,
    C;
  }

  public static void main(String[] args) {
    LoggerFactory.useSlf4jLogging(true);

    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .withIntCompressed(true)
            .withCompatibleMode(CompatibleMode.SCHEMA_CONSISTENT)
            .requireClassRegistration(true)
            .build();
    fury.register(FloatPrimitive.class);
    fury.register(FloatObject.class);
    fury.register(FloatObjectNull.class);
    fury.register(BytePrimitive.class);
    fury.register(ShortPrimitive.class);
    fury.register(IntPrimitive.class);
    fury.register(EnumPrimitive.class);

    System.out.printf("Float primitive: %d bytes%n", fury.serialize(new FloatPrimitive()).length);
    System.out.printf("Float object   : %d bytes%n", fury.serialize(new FloatObject()).length);
    System.out.printf("Float null     : %d bytes%n", fury.serialize(new FloatObjectNull()).length);
    System.out.printf("Byte  primitive: %d bytes%n", fury.serialize(new BytePrimitive()).length);
    System.out.printf("Short primitive: %d bytes%n", fury.serialize(new ShortPrimitive()).length);
    System.out.printf("Int   primitive: %d bytes%n", fury.serialize(new IntPrimitive()).length);
    System.out.printf("Enum  primitive: %d bytes%n", fury.serialize(EnumPrimitive.A).length);
  }
}
