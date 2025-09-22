package org.molgenis.vipannotate;

import org.apache.fory.Fory;

public class App {
  static void main() {
    Fory fory = ForyFactory.createFory();

    System.out.println("serialize/deserialize int wrapper class");
    {
      ForyFactory.IntWrapperClass obj = new ForyFactory.IntWrapperClass(1);
      byte[] bytes = fory.serializeJavaObject(obj);
      ForyFactory.IntWrapperClass deserialized =
          fory.deserializeJavaObject(bytes, ForyFactory.IntWrapperClass.class);
      System.out.println("toString():" + deserialized);
    }
    System.out.println("success");

    System.out.println("serialize/deserialize int wrapper record");
    {
      ForyFactory.IntWrapperRecord obj = new ForyFactory.IntWrapperRecord(1);
      byte[] bytes = fory.serializeJavaObject(obj);
      ForyFactory.IntWrapperRecord deserialized =
          fory.deserializeJavaObject(bytes, ForyFactory.IntWrapperRecord.class);
      System.out.println("toString():" + deserialized);
    }
    System.out.println("success");
  }
}
