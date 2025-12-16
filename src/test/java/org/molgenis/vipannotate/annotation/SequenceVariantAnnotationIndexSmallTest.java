package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.util.IndexRange;

@SuppressWarnings({"NullableProblems", "DataFlowIssue", "NullAway"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexSmallTest {
  @Mock SequenceVariantEncoder<SequenceVariant> encoder;

  @Test
  void isEmpty() {
    SequenceVariantAnnotationIndexSmall<SequenceVariant> sequenceVariantAnnotationIndexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[0]);
    assertTrue(sequenceVariantAnnotationIndexSmall.isEmpty());
  }

  @Test
  void isEmptyFalse() {
    SequenceVariantAnnotationIndexSmall<SequenceVariant> sequenceVariantAnnotationIndexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[] {0});
    assertFalse(sequenceVariantAnnotationIndexSmall.isEmpty());
  }

  private static Stream<Arguments> findIndexesProvider() {
    return Stream.of(
        Arguments.of(2, new int[] {0, 1, 2, 3, 4, 4, 5}, 7, new IndexRange(2, 2)),
        Arguments.of(4, new int[] {0, 1, 2, 3, 4, 4, 5}, 7, new IndexRange(4, 5)),
        Arguments.of(4, new int[] {0, 1, 2, 3, 4, 4, 5}, 5, new IndexRange(4, 4)),
        Arguments.of(4, new int[] {0, 1, 2, 3, 4, 4, 5}, 4, null),
        Arguments.of(4, new int[0], 0, null));
  }

  @ParameterizedTest
  @MethodSource("findIndexesProvider")
  void findIndexes(int encodedVariant, int[] encodedVariants, int length, IndexRange indexRange) {
    SequenceVariantAnnotationIndexSmall<SequenceVariant> sequenceVariantAnnotationIndexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, encodedVariants, length);
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    if (length != 0) {
      EncodedSequenceVariant encodedSequenceVariant = mock(EncodedSequenceVariant.class);
      when(encodedSequenceVariant.getSmall()).thenReturn(encodedVariant);
      when(encoder.encode(sequenceVariant)).thenReturn(encodedSequenceVariant);
    }
    assertEquals(indexRange, sequenceVariantAnnotationIndexSmall.findIndexes(sequenceVariant));
  }

  @Test
  void reset() {
    SequenceVariantAnnotationIndexSmall<SequenceVariant> sequenceVariantAnnotationIndexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[] {0});
    sequenceVariantAnnotationIndexSmall.reset();
    assertTrue(sequenceVariantAnnotationIndexSmall.isEmpty());
  }

  @Test
  void resetWithContent() {
    SequenceVariantAnnotationIndexSmall<SequenceVariant> sequenceVariantAnnotationIndexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[0]);
    sequenceVariantAnnotationIndexSmall.reset(new int[] {0}, 1);
    assertFalse(sequenceVariantAnnotationIndexSmall.isEmpty());
  }
}
