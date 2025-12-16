package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PosTest {
  @Test
  void get() {
    assertEquals(123, Pos.wrap("123").get());
  }

  @Test
  void getAfterReset() {
    Pos pos = Pos.wrap("123");
    pos.reset("456");
    assertEquals(456, pos.get());
  }

  @Test
  void testToString() {
    assertEquals("POS=123", Pos.wrap("123").toString());
  }
}
