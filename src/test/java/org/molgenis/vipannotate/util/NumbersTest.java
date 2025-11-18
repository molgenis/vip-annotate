package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NumbersTest {
  // int positive
  @Test
  void validatePositiveInt() {
    assertDoesNotThrow(() -> Numbers.validatePositive(1));
  }

  @Test
  void validatePositiveIntInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validatePositive(0));
  }

  @Test
  void requirePositiveInt() {
    assertEquals(1, Numbers.requirePositive(1));
  }

  @Test
  void requirePositiveIntInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.requirePositive(0));
  }

  // int non-negative or null
  @Test
  void validateNonNegativeOrNullInt() {
    assertDoesNotThrow(() -> Numbers.validateNonNegativeOrNull(0));
  }

  @Test
  void validateNonNegativeOrNullIntInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validateNonNegativeOrNull(-1));
  }

  @Test
  void requireNonNegativeOrNullIntNull() {
    assertNull(Numbers.requireNonNegativeOrNull((Integer) null));
  }

  @Test
  void requireNonNegativeOrNullInt() {
    assertEquals(0, Numbers.requireNonNegativeOrNull(0));
  }

  @Test
  void requireNonNegativeOrNullIntInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.requireNonNegativeOrNull(-1));
  }

  // int non-negative
  @Test
  void validateNonNegativeInt() {
    assertDoesNotThrow(() -> Numbers.validateNonNegative(0));
  }

  @Test
  void validateNonNegativeIntInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validateNonNegative(-1));
  }

  @Test
  void requireNonNegativeInt() {
    assertEquals(0, Numbers.requireNonNegative(0));
  }

  @Test
  void requireNonNegativeIntInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.requireNonNegative(-1));
  }

  // long positive
  @Test
  void validatePositiveLong() {
    assertDoesNotThrow(() -> Numbers.validatePositive(1L));
  }

  @Test
  void validatePositiveLongInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validatePositive(0L));
  }

  @Test
  void requirePositiveLong() {
    assertEquals(1L, Numbers.requirePositive(1L));
  }

  @Test
  void requirePositiveLongInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.requirePositive(0L));
  }

  // long non-negative
  @Test
  void validateNonNegativeLong() {
    assertDoesNotThrow(() -> Numbers.validateNonNegative(0L));
  }

  @Test
  void validateNonNegativeLongInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validateNonNegative(-1L));
  }

  @Test
  void requireNonNegativeLong() {
    assertEquals(0L, Numbers.requireNonNegative(0L));
  }

  @Test
  void requireNonNegativeLongInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.requireNonNegative(-1L));
  }

  // double non-negative
  @Test
  void validateNonNegativeDouble() {
    assertDoesNotThrow(() -> Numbers.validateNonNegative(0d));
  }

  @Test
  void validateNonNegativeDoubleInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validateNonNegative(-1d));
  }

  @Test
  void requireNonNegativeDouble() {
    assertEquals(0, Numbers.requireNonNegative(0d));
  }

  @Test
  void requireNonNegativeDoubleInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.requireNonNegative(-1d));
  }

  // double non-negative or null
  @Test
  void validateNonNegativeOrNullDouble() {
    assertDoesNotThrow(() -> Numbers.validateNonNegativeOrNull(0d));
  }

  @Test
  void validateNonNegativeOrNullDoubleInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validateNonNegativeOrNull(-1d));
  }

  @Test
  void requireNonNegativeOrNullDoubleNull() {
    assertNull(Numbers.requireNonNegativeOrNull((Double) null));
  }

  @Test
  void requireNonNegativeOrNullDouble() {
    assertEquals(0d, Numbers.requireNonNegativeOrNull(0d));
  }

  @Test
  void requireNonNegativeOrNullDoubleInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.requireNonNegativeOrNull(-1d));
  }

  @Test
  void validateIntervalOrNullValid() {
    assertDoesNotThrow(() -> Numbers.validateIntervalOrNull((byte) 0, (byte) -1, (byte) 1));
  }

  @Test
  void validateIntervalOrNullValidNull() {
    assertDoesNotThrow(() -> Numbers.validateIntervalOrNull(null, (byte) -1, (byte) 1));
  }

  @Test
  void validateIntervalOrNullInvalidFrom() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Numbers.validateIntervalOrNull((byte) -2, (byte) -1, (byte) 1));
  }

  @Test
  void validateIntervalOrNullInvalidTo() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Numbers.validateIntervalOrNull((byte) 2, (byte) -1, (byte) 1));
  }

  @Test
  void validateUnitIntervalValid() {
    assertDoesNotThrow(() -> Numbers.validateUnitInterval(0.5d));
  }

  @Test
  void validateUnitIntervalInvalidTooLow() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validateUnitInterval(-0.1d));
  }

  @Test
  void validateUnitIntervalInvalidTooHigh() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.validateUnitInterval(1.1d));
  }

  @Test
  void safeIntToByte() {
    assertEquals((byte) 64, Numbers.safeIntToByte(64));
  }

  @Test
  void safeIntToByteTooLow() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.safeIntToByte(-256));
  }

  @Test
  void safeIntToByteTooHigh() {
    assertThrows(IllegalArgumentException.class, () -> Numbers.safeIntToByte(256));
  }

  @Test
  void nextPowerOf2() {
    assertAll(
        () -> assertEquals(1, Numbers.nextPowerOf2(0)),
        () -> assertEquals(1, Numbers.nextPowerOf2(1)),
        () -> assertEquals(2, Numbers.nextPowerOf2(2)),
        () -> assertEquals(4, Numbers.nextPowerOf2(3)),
        () -> assertEquals(8, Numbers.nextPowerOf2(5)));
  }
}
