package org.molgenis.vipannotate.annotation.phylop;

import org.junit.jupiter.api.Test;

// FIXME actually test something
class PhyloPScoreCodecTest {
  @Test
  public void test() {
    System.out.println(PhyloPScoreCodec.decode(PhyloPScoreCodec.encode("1.234")));
  }
}
