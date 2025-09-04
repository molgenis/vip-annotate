package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class SequenceVariantEncoderTest {
  @SuppressWarnings("DataFlowIssue")
  @Test
  void isSmallVariantStructural() {
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(sequenceVariant.getType()).thenReturn(SequenceVariantType.STRUCTURAL);
    assertFalse(SequenceVariantEncoder.isSmallVariant(sequenceVariant));
  }

  @SuppressWarnings("DataFlowIssue")
  @Test
  void isSmallVariantOther() {
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(sequenceVariant.getType()).thenReturn(SequenceVariantType.OTHER);
    assertFalse(SequenceVariantEncoder.isSmallVariant(sequenceVariant));
  }
}
