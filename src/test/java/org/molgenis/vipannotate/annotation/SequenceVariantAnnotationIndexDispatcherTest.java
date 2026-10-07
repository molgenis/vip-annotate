package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type.POS_20_BIT;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.util.IndexRange;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexDispatcherTest {
  @Mock private SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall;
  @Mock private SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig;
  private SequenceVariantAnnotationIndexDispatcher<SequenceVariant> indexDispatcher;

  @BeforeEach
  void setUp() {
    indexDispatcher = new SequenceVariantAnnotationIndexDispatcher<>();
    indexDispatcher.register(POS_20_BIT, indexSmall);
    indexDispatcher.register(EncodedSequenceVariant.Type.BIG, indexBig);
  }

  private static Stream<Arguments> isEmptyProvider() {
    return Stream.of(
        Arguments.of(true, true, true),
        Arguments.of(true, false, false),
        Arguments.of(false, true, false),
        Arguments.of(false, false, false));
  }

  @ParameterizedTest
  @MethodSource("isEmptyProvider")
  void isEmpty(boolean isSmallEmpty, boolean isBigEmpty, boolean isEmpty) {
    if (isSmallEmpty) {
      when(indexSmall.isEmpty()).thenReturn(true);
      if (isBigEmpty) {
        when(indexBig.isEmpty()).thenReturn(true);
      }
    }

    assertEquals(isEmpty, indexDispatcher.isEmpty());
  }

  @Test
  void findIndexesSmall() {
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(sequenceVariant.getType()).thenReturn(SequenceVariantType.SNV);
    when(sequenceVariant.getAlt()).thenReturn(new AltAllele("A"));
    IndexRange indexRange = mock(IndexRange.class);
    when(indexSmall.findIndexes(sequenceVariant, 123)).thenReturn(indexRange);
    assertEquals(
        indexRange, indexDispatcher.findIndexes(sequenceVariant, new PositionEncoding(123, 12)));
  }

  @Test
  void findIndexesBigSequenceVariantNoBigIndex() {
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(sequenceVariant.getType()).thenReturn(SequenceVariantType.SNV);
    when(sequenceVariant.getAlt()).thenReturn(new AltAllele("N"));

    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> indexDispatcher =
        new SequenceVariantAnnotationIndexDispatcher<>();
    indexDispatcher.register(POS_20_BIT, indexSmall);

    assertNull(indexDispatcher.findIndexes(sequenceVariant, new PositionEncoding(123, 12)));
  }

  @Test
  void findIndexesEmpty() {
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(indexSmall.isEmpty()).thenReturn(true);
    when(indexBig.isEmpty()).thenReturn(true);
    assertNull(indexDispatcher.findIndexes(sequenceVariant, new PositionEncoding(123, 12)));
  }

  @Test
  void getAnnotationIndex() {
    assertEquals(indexDispatcher.getAnnotationIndex(POS_20_BIT), indexSmall);
  }

  @Test
  void getAnnotationIndexNotExists() {
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> indexDispatcher =
        new SequenceVariantAnnotationIndexDispatcher<>();
    assertThrows(
        EnumConstantNotPresentException.class,
        () -> indexDispatcher.getAnnotationIndex(POS_20_BIT));
  }

  @Test
  void reset() {
    indexDispatcher.reset();
    assertAll(() -> verify(indexSmall).reset(), () -> verify(indexBig).reset());
  }
}
