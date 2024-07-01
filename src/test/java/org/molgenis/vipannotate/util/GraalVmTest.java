package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraalVmTest {
  private String propertyValue;

  @BeforeEach
  void setUp() {
    propertyValue = System.getProperty("org.graalvm.nativeimage.imagecode");
  }

  @AfterEach
  void tearDown() {
    if (propertyValue != null) {
      System.setProperty("org.graalvm.nativeimage.imagecode", propertyValue);
    }
  }

  @Test
  void isGraalVmRuntime() {
    System.setProperty("org.graalvm.nativeimage.imagecode", "runtime");
    assertTrue(GraalVm.isGraalVmRuntime());
  }

  @Test
  void isGraalVmRuntimeFalseNoProperty() {
    System.clearProperty("org.graalvm.nativeimage.imagecode");
    assertFalse(GraalVm.isGraalVmRuntime());
  }

  @Test
  void isGraalVmRuntimeFalsePropertyValueMismatch() {
    System.setProperty("org.graalvm.nativeimage.imagecode", "buildtime");
    assertFalse(GraalVm.isGraalVmRuntime());
  }
}
