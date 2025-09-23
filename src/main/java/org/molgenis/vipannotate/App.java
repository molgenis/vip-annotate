package org.molgenis.vipannotate;

import org.apache.fory.Fory;

public class App {
  /**
   *
   *
   * <pre>
   * SequenceVariantAnnotationIndexSmall small = new SequenceVariantAnnotationIndexSmall();
   * SequenceVariantAnnotationIndexBig big = new SequenceVariantAnnotationIndexBig(new BigInteger[] {BigInteger.ZERO, BigInteger.ONE});
   * SequenceVariantAnnotationIndex obj = new SequenceVariantAnnotationIndex(small, big);
   * byte[] bytes = fory.serializeJavaObject(obj);
   * </pre>
   */
  private static final byte[] BYTES =
      new byte[] {
        (byte) 0xFF,
        (byte) 0xFF,
        (byte) 0xAA,
        (byte) 0x02,
        (byte) 0xFF,
        (byte) 0x19,
        (byte) 0x04,
        (byte) 0x24,
        (byte) 0x15,
        (byte) 0x06,
        (byte) 0x98,
        (byte) 0x09,
        (byte) 0x9C,
        (byte) 0x12,
        (byte) 0x02,
        (byte) 0x6A,
        (byte) 0xD9,
        (byte) 0x03,
        (byte) 0x44,
        (byte) 0x6A,
        (byte) 0x62,
        (byte) 0x0C,
        (byte) 0x22,
        (byte) 0x20,
        (byte) 0x04,
        (byte) 0xFF,
        (byte) 0x5C,
        (byte) 0x01,
        (byte) 0x00,
        (byte) 0xFF,
        (byte) 0x5C,
        (byte) 0x01,
        (byte) 0x01,
        (byte) 0xFF,
        (byte) 0xAC,
        (byte) 0x02
      };

  static void main() {
    Fory fory = ForyFactory.createFory();

    // issue #2: fails for GraalVm native image
    /*
    serialize/deserialize
    Exception in thread "main" org.apache.fory.exception.DeserializationException: Failed to deserialize input
            at org.apache.fory.util.ExceptionUtils.handleReadFailed(ExceptionUtils.java:66)
            at org.apache.fory.Fory.deserializeJavaObject(Fory.java:1226)
            at org.apache.fory.Fory.deserializeJavaObject(Fory.java:1197)
            at org.molgenis.vipannotate.App.main(App.java:62)
            at java.base@25/java.lang.invoke.LambdaForm$DMH/s4b9cede1.invokeStaticInit(LambdaForm$DMH)
    Caused by: java.lang.IllegalArgumentException: Type org.molgenis.vipannotate.SequenceVariantAnnotationIndex is instantiated reflectively but was never registered. Register the type by adding "unsafeAllocated" for the type in reflect-config.json.
            at org.graalvm.nativeimage.builder/com.oracle.svm.core.graal.snippets.SubstrateAllocationSnippets.slowPathHubOrUnsafeInstantiationError(SubstrateAllocationSnippets.java:388)
            at org.molgenis.vipannotate.SequenceVariantAnnotationIndexForyCodec_0.read(SequenceVariantAnnotationIndexForyCodec_0.java:134)
            at org.apache.fory.Fory.readDataInternal(Fory.java:1052)
            at org.apache.fory.Fory.deserializeJavaObject(Fory.java:1220)
            ... 3 more
        */
    System.out.println("serialize/deserialize");
    {
      SequenceVariantAnnotationIndex deserialized =
          fory.deserializeJavaObject(BYTES, SequenceVariantAnnotationIndex.class);
      System.out.println("toString():" + deserialized);
    }
    System.out.println("success");
  }
}
