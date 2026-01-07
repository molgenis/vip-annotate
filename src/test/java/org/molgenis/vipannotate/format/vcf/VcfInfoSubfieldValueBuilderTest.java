package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VcfInfoSubfieldValueBuilderTest {
  private VcfInfoSubfieldValueBuilder vcfInfoSubfieldValueBuilder;

  @BeforeEach
  void setUp() {
    vcfInfoSubfieldValueBuilder = new VcfInfoSubfieldValueBuilder();
  }

  @Test
  void appendValueChar() {
    vcfInfoSubfieldValueBuilder.appendValue('x');
    vcfInfoSubfieldValueBuilder.appendValue('y');
    assertEquals("x,y", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendValueString() {
    vcfInfoSubfieldValueBuilder.appendValue("str0");
    vcfInfoSubfieldValueBuilder.appendValue("str1");
    assertEquals("str0,str1", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendValueInt() {
    vcfInfoSubfieldValueBuilder.appendValue(0);
    vcfInfoSubfieldValueBuilder.appendValue(1);
    assertEquals("0,1", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendValueDouble() {
    vcfInfoSubfieldValueBuilder.appendValue(0.123, 1);
    vcfInfoSubfieldValueBuilder.appendValue(0.236, 2);
    assertEquals("0.1,0.24", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendValueMissing() {
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    assertAll(
        () -> assertEquals(".,.", vcfInfoSubfieldValueBuilder.build().toString()),
        () -> assertTrue(vcfInfoSubfieldValueBuilder.isEmptyValue()));
  }

  @Test
  void appendValueMissingSome() {
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    vcfInfoSubfieldValueBuilder.appendValue("str");
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    assertEquals(".,str,.", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendRawString() {
    vcfInfoSubfieldValueBuilder.startRawValue();
    vcfInfoSubfieldValueBuilder.appendRaw("str0&str1");
    vcfInfoSubfieldValueBuilder.endRawValue();
    vcfInfoSubfieldValueBuilder.appendValue("str2");
    assertEquals("str0&str1,str2", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendRawChar() {
    vcfInfoSubfieldValueBuilder.appendValue('x');
    vcfInfoSubfieldValueBuilder.startRawValue();
    vcfInfoSubfieldValueBuilder.appendRaw('y');
    vcfInfoSubfieldValueBuilder.endRawValue();
    assertEquals("x,y", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendRawInt() {
    vcfInfoSubfieldValueBuilder.startRawValue();
    vcfInfoSubfieldValueBuilder.appendRaw(1);
    vcfInfoSubfieldValueBuilder.appendRaw('&');
    vcfInfoSubfieldValueBuilder.appendRaw(2);

    vcfInfoSubfieldValueBuilder.endRawValue();
    assertEquals("1&2", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void appendRawDouble() {
    vcfInfoSubfieldValueBuilder.startRawValue();
    vcfInfoSubfieldValueBuilder.appendRaw(1.23, 2);
    vcfInfoSubfieldValueBuilder.appendRaw('&');
    vcfInfoSubfieldValueBuilder.appendRaw(2.34, 1);
    vcfInfoSubfieldValueBuilder.endRawValue();
    assertEquals("1.23&2.3", vcfInfoSubfieldValueBuilder.build().toString());
  }

  @Test
  void isEmptyValue() {
    assertTrue(vcfInfoSubfieldValueBuilder.isEmptyValue());
  }

  @Test
  void isEmptyValueSingle() {
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    assertTrue(vcfInfoSubfieldValueBuilder.isEmptyValue());
  }

  @Test
  void isEmptyValueMultiple() {
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    assertTrue(vcfInfoSubfieldValueBuilder.isEmptyValue());
  }

  @Test
  void isEmptyValueFalse() {
    vcfInfoSubfieldValueBuilder.appendValue('x');
    assertFalse(vcfInfoSubfieldValueBuilder.isEmptyValue());
  }

  @Test
  void isEmptyValueRawFalse() {
    vcfInfoSubfieldValueBuilder.startRawValue();
    vcfInfoSubfieldValueBuilder.appendRaw(1);
    vcfInfoSubfieldValueBuilder.endRawValue();
    assertFalse(vcfInfoSubfieldValueBuilder.isEmptyValue());
  }

  @Test
  void reset() {
    vcfInfoSubfieldValueBuilder.appendValue("str0");
    vcfInfoSubfieldValueBuilder.appendValue(1);
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    vcfInfoSubfieldValueBuilder.reset();
    vcfInfoSubfieldValueBuilder.appendValue("str1");
    vcfInfoSubfieldValueBuilder.appendValue(2);
    vcfInfoSubfieldValueBuilder.appendValueMissing();
    assertEquals("str1,2,.", vcfInfoSubfieldValueBuilder.build().toString());
  }
}
