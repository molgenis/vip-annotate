package org.molgenis.vcf.annotate.db.chrpos.phylop;

import org.junit.jupiter.api.Test;

class PhyloPScoreCodecTest {
  @Test
  public void test() {

    System.out.println(PhyloPScoreCodec.decode(PhyloPScoreCodec.encode("1.234")));
  }
}
