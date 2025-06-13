package org.molgenis.vipannotate.db.chrpos.phylop;

import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.annotation.phylop.PhyloPScoreCodec;

class PhyloPScoreCodecTest {
  @Test
  public void test() {

    System.out.println(PhyloPScoreCodec.decode(PhyloPScoreCodec.encode("1.234")));
  }
}
