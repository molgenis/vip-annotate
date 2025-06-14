package org.molgenis.vipannotate.annotation.phylop;

import org.junit.jupiter.api.Test;

// FIXME actually test something
class PhyloPAnnotationDataCodecTest {
  @Test
  public void test() {
    System.out.println(
        new PhyloPAnnotationDataCodec().decode(PhyloPAnnotationDataCodec.encode("1.234")));
  }
}
