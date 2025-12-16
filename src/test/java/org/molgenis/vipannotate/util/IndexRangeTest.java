package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IndexRangeTest {
  @Test
  void startNegative() {
    assertThrows(IllegalArgumentException.class, () -> new IndexRange(-1, 1));
  }

  @Test
  void startGreaterThanEnd() {
    assertThrows(IllegalArgumentException.class, () -> new IndexRange(2, 1));
  }
}
