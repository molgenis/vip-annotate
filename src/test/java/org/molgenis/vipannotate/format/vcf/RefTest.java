package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RefTest {

  @Test
  void getBases() {
    assertEquals("ACgt", Ref.wrap("ACgt").getBases().toString());
  }

  @Test
  void getBasesAfterReset() {
    Ref ref = Ref.wrap("ACGT");
    ref.reset("A");
    assertEquals("A", ref.getBases().toString());
  }

  @Test
  void getBaseCount() {
    assertEquals(4, Ref.wrap("ACGT").getBaseCount());
  }

  @Test
  void getBaseCountAfterReset() {
    Ref ref = Ref.wrap("ACGT");
    ref.reset("A");
    assertEquals(1, ref.getBaseCount());
  }

  @Test
  void testToString() {
    assertEquals("REF=ACGT", Ref.wrap("ACGT").toString());
  }
}
