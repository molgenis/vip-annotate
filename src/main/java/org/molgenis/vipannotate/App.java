package org.molgenis.vipannotate;

import org.apache.fory.Fory;

public class App {
  static void main() {
    Fory fory = ForyFactory.createFory();

    System.out.println("serialize/deserialize");
    {
      ForyFactory.ObjImpl objImpl = new ForyFactory.ObjImpl();
      byte[] bytes = fory.serializeJavaObject(objImpl);
      ForyFactory.ObjImpl deserialized =
          fory.deserializeJavaObject(bytes, ForyFactory.ObjImpl.class);
      System.out.println("toString():" + deserialized);
    }
    System.out.println("success");

    System.out.println("serialize/deserialize");
    {
      ForyFactory.ObjConcrete objConcrete = new ForyFactory.ObjConcrete();
      byte[] bytes = fory.serializeJavaObject(objConcrete);
      ForyFactory.ObjConcrete deserialized =
          fory.deserializeJavaObject(bytes, ForyFactory.ObjConcrete.class);
      System.out.println("toString():" + deserialized);
    }
    System.out.println("success");

    System.out.println("serialize/deserialize");
    {
      ForyFactory.ObjGenericImpl ObjGenericImpl = new ForyFactory.ObjGenericImpl();
      byte[] bytes = fory.serializeJavaObject(ObjGenericImpl);
      ForyFactory.ObjGenericImpl deserialized =
          fory.deserializeJavaObject(bytes, ForyFactory.ObjGenericImpl.class);
      System.out.println("toString():" + deserialized);
    }
    System.out.println("success");
  }
}
