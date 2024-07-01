package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GenotypeTest {
  @Test
  void testToString() {
    assertEquals("GENOTYPE=GT 0|1", Genotype.wrap("GT\t0|1").toString());
  }
}
