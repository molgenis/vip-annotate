package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RankBitmapTest {

  @Test
  void emptyBitmap() {
    RankBitmap bitmap = RankBitmap.builder(0).build();

    assertEquals(0, bitmap.size());
    assertEquals(0, bitmap.rank1(0));
  }

  @Test
  void rankCountsSetBitsBeforeIndex() {
    RankBitmap bitmap = RankBitmap.builder(10).set(1).set(4).set(7).build();

    assertEquals(0, bitmap.rank1(0));
    assertEquals(0, bitmap.rank1(1));
    assertEquals(1, bitmap.rank1(2));
    assertEquals(1, bitmap.rank1(4));
    assertEquals(2, bitmap.rank1(5));
    assertEquals(2, bitmap.rank1(7));
    assertEquals(3, bitmap.rank1(8));
    assertEquals(3, bitmap.rank1(10));
  }

  @Test
  void isSetReturnsCorrectValues() {
    RankBitmap bitmap = RankBitmap.builder(8).set(0).set(3).set(7).build();

    assertTrue(bitmap.isSet(0));
    assertFalse(bitmap.isSet(1));
    assertTrue(bitmap.isSet(3));
    assertTrue(bitmap.isSet(7));
  }

  @Test
  void valueIndexReturnsCompactedIndex() {
    RankBitmap bitmap = RankBitmap.builder(10).set(1).set(4).set(7).build();

    assertEquals(0, bitmap.valueIndex(1));
    assertEquals(1, bitmap.valueIndex(4));
    assertEquals(2, bitmap.valueIndex(7));
  }

  @Test
  void valueIndexRejectsUnsetBit() {
    RankBitmap bitmap = RankBitmap.builder(5).set(2).build();

    assertThrows(IllegalStateException.class, () -> bitmap.valueIndex(1));
  }

  @Test
  void builderCanClearBit() {
    RankBitmap bitmap = RankBitmap.builder(8).set(2).set(5).clear(2).build();

    assertFalse(bitmap.isSet(2));
    assertTrue(bitmap.isSet(5));
    assertEquals(1, bitmap.rank1(8));
  }

  @Test
  void rejectsOutOfBoundsIndices() {
    RankBitmap bitmap = RankBitmap.builder(8).build();

    assertThrows(IndexOutOfBoundsException.class, () -> bitmap.isSet(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> bitmap.isSet(8));
    assertThrows(IndexOutOfBoundsException.class, () -> bitmap.rank1(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> bitmap.rank1(9));
    assertThrows(IndexOutOfBoundsException.class, () -> RankBitmap.builder(8).set(8));
  }

  @Test
  void rankWorksAcrossBlockBoundary() {
    RankBitmap bitmap =
        RankBitmap.builder(1025).set(0).set(511).set(512).set(513).set(1024).build();

    assertEquals(1, bitmap.rank1(511));
    assertEquals(2, bitmap.rank1(512));
    assertEquals(3, bitmap.rank1(513));
    assertEquals(4, bitmap.rank1(514));
    assertEquals(4, bitmap.rank1(1024));
    assertEquals(5, bitmap.rank1(1025));
  }

  @Test
  void supportsPartialFinalWord() {
    RankBitmap bitmap = RankBitmap.builder(65).set(0).set(63).set(64).build();

    assertEquals(2, bitmap.rank1(64));
    assertEquals(3, bitmap.rank1(65));
  }

  @Test
  void serializedArraysAreDefensivelyCopied() {
    RankBitmap original = RankBitmap.builder(8).set(2).set(5).build();

    long[] serializedBitmap = original.bitmap();
    int[] serializedCheckpoints = original.checkpoints();

    RankBitmap restored =
        RankBitmap.fromSerialized(serializedBitmap, serializedCheckpoints, original.size());

    serializedBitmap[0] = 0;
    serializedCheckpoints[0] = 100;

    assertTrue(restored.isSet(2));
    assertTrue(restored.isSet(5));
    assertEquals(2, restored.rank1(8));
  }

  @Test
  void rejectsInvalidBitmapLength() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RankBitmap.fromSerialized(new long[0], new int[0], 65));
  }

  @Test
  void rejectsInvalidCheckpointCount() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RankBitmap.fromSerialized(new long[9], new int[0], 513));
  }
}
