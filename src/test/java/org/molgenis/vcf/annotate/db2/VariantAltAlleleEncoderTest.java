// FIXME
// package org.molgenis.vcf.annotate.db2;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.nio.charset.StandardCharsets;
// import org.junit.jupiter.api.Test;
// import org.molgenis.vcf.annotate.db2.exact.VariantAltAlleleEncoder;
//
// class VariantAltAlleleEncoderTest {
//  @Test
//  void isSmallVariantRef1Alt1() {
//    assertTrue(VariantAltAlleleEncoder.isSmallVariant(1, 1));
//  }
//
//  @Test
//  void isSmallVariantRef4Alt4() {
//    assertTrue(VariantAltAlleleEncoder.isSmallVariant(4, 4));
//  }
//
//  @Test
//  void isSmallVariantRef5Alt1() {
//    assertFalse(VariantAltAlleleEncoder.isSmallVariant(5, 1));
//  }
//
//  @Test
//  void isSmallVariantRef1Alt5() {
//    assertFalse(VariantAltAlleleEncoder.isSmallVariant(1, 5));
//  }
//
//  @Test
//  void isSmallVariantRef5Alt5() {
//    assertFalse(VariantAltAlleleEncoder.isSmallVariant(5, 5));
//  }
//
//  @Test
//  void isSmallVariantRef0Alt1() {
//    assertThrows(
//        IllegalArgumentException.class, () -> VariantAltAlleleEncoder.isSmallVariant(0, 1));
//  }
//
//  @Test
//  void isSmallVariantRef1Alt0() {
//    assertThrows(
//        IllegalArgumentException.class, () -> VariantAltAlleleEncoder.isSmallVariant(1, 0));
//  }
//
//  @Test
//  void encodeSmallPos1Ref1AltA() {
//    assertEquals(
//        0b1000000000000,
//        VariantAltAlleleEncoder.encodeSmall(1, 1, "A".getBytes(StandardCharsets.UTF_8)));
//  }
//
//  @Test
//  void encodeSmallPos1048579Ref1AltACGT() {
//    assertEquals(
//        0b11111111100100,
//        VariantAltAlleleEncoder.encodeSmall(1048579, 4, "ACGT".getBytes(StandardCharsets.UTF_8)));
//  }
//
//  @Test
//  void encodeSmallPos1Ref1AltN() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> VariantAltAlleleEncoder.encodeSmall(1, 1, "N".getBytes(StandardCharsets.UTF_8)));
//  }
//
//  @Test
//  void encodeSmallPos0Ref1AltA() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> VariantAltAlleleEncoder.encodeSmall(0, 1, "A".getBytes(StandardCharsets.UTF_8)));
//  }
//
//  @Test
//  void encodeSmallPos1Ref0AltA() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> VariantAltAlleleEncoder.encodeSmall(1, 0, "A".getBytes(StandardCharsets.UTF_8)));
//  }
//
//  @Test
//  void encodeSmallPos1Ref5AltA() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> VariantAltAlleleEncoder.encodeSmall(1, 5, "A".getBytes(StandardCharsets.UTF_8)));
//  }
//
//  @Test
//  void encodeSmallPos1Ref1Alt0() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> VariantAltAlleleEncoder.encodeSmall(1, 1, new byte[0]));
//  }
//
//  @Test
//  void encodeSmallPos1Ref1AltACTGA() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> VariantAltAlleleEncoder.encodeSmall(1, 1,
// "ACTGA".getBytes(StandardCharsets.UTF_8)));
//  }
//
//  @Test
//  void encodeBig() {
//    throw new RuntimeException(); // FIXME add test cases
//  }
// }
