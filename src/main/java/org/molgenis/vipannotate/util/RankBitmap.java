package org.molgenis.vipannotate.util;

import java.util.Arrays;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public final class RankBitmap {
  private static final int BLOCK_BITS = 512;
  private static final int WORDS_PER_BLOCK = BLOCK_BITS / Long.SIZE;

  private final int size;
  private final long[] bitmap;
  private final int[] checkpoints;

  private RankBitmap(int size, long[] bitmap, int[] checkpoints) {
    this.size = size;
    this.bitmap = bitmap;
    this.checkpoints = checkpoints;
  }

  public static Builder builder(int size) {
    return new Builder(size);
  }

  public static RankBitmap fromSerialized(long[] bitmap, int[] checkpoints, int size) {
    validateBitmap(bitmap, size);

    int expectedBlocks = (int) (((long) size + BLOCK_BITS - 1) / BLOCK_BITS);

    if (checkpoints.length != expectedBlocks) {
      throw new IllegalArgumentException("Invalid checkpoint count");
    }

    return new RankBitmap(
        size, Arrays.copyOf(bitmap, bitmap.length), Arrays.copyOf(checkpoints, checkpoints.length));
  }

  public int size() {
    return size;
  }

  public boolean isSet(int index) {
    checkIndex(index);
    return (bitmap[index >>> 6] & (1L << (index & 63))) != 0;
  }

  /** Counts set bits in [0, index). */
  public int rank1(int index) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }

    if (index == size && size == 0) {
      return 0;
    }

    int block = index == size ? (size - 1) / BLOCK_BITS : index / BLOCK_BITS;

    return checkpoints[block] + countBlockPrefix(block, index);
  }

  /** Returns the compacted-array index for a present entry. */
  public int valueIndex(int index) {
    if (!isSet(index)) {
      throw new IllegalStateException("Bit is not set at index " + index);
    }
    return rank1(index);
  }

  public long[] bitmap() {
    return Arrays.copyOf(bitmap, bitmap.length);
  }

  public int[] checkpoints() {
    return Arrays.copyOf(checkpoints, checkpoints.length);
  }

  private int countBlockPrefix(int block, int endExclusive) {
    int count = 0;
    int startWord = block * WORDS_PER_BLOCK;
    int endWord = endExclusive >>> 6;
    int bitOffset = endExclusive & 63;

    for (int word = startWord; word < endWord; word++) {
      count += Long.bitCount(bitmap[word]);
    }

    if (bitOffset != 0) {
      count += Long.bitCount(bitmap[endWord] & ((1L << bitOffset) - 1));
    }

    return count;
  }

  private static int[] buildCheckpoints(long[] bitmap, int size) {
    int nrBlocks = (int) (((long) size + BLOCK_BITS - 1) / BLOCK_BITS);
    int[] checkpoints = new int[nrBlocks];

    int count = 0;

    for (int block = 0; block < nrBlocks; block++) {
      checkpoints[block] = count;

      int startWord = block * WORDS_PER_BLOCK;
      int endWord = Math.min(startWord + WORDS_PER_BLOCK, bitmap.length);

      for (int word = startWord; word < endWord; word++) {
        count += Long.bitCount(bitmap[word]);
      }
    }

    return checkpoints;
  }

  private static void validateBitmap(long[] bitmap, int size) {
    if (size < 0) {
      throw new IllegalArgumentException("size must be non-negative");
    }

    int expectedWords = (int) (((long) size + 63) / 64);
    if (bitmap.length != expectedWords) {
      throw new IllegalArgumentException("Invalid bitmap length");
    }
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }
  }

  public static RankBitmap readFrom(MemoryDecoder memDecoder) {
    memDecoder.unwrap().flip(); // FIXME should not happen here, probably bug somewhere else

    int size = memDecoder.getVarInt();
    long[] bitmap = memDecoder.getLongArray();
    int[] checkpoints = memDecoder.getIntArray(); // FIXME StreamVByte.deltaEncode
    return new RankBitmap(size, bitmap, checkpoints);
  }

  public void writeInto(MemoryEncoder memEncoder) {
    memEncoder.putVarInt(size);
    memEncoder.putLongArray(bitmap);
    memEncoder.putIntArray(checkpoints); // FIXME StreamVByte.deltaDecode
  }

  public static final class Builder {
    private final int size;
    private final long[] bitmap;

    private Builder(int size) {
      if (size < 0) {
        throw new IllegalArgumentException("size must be non-negative");
      }

      this.size = size;
      this.bitmap = new long[(int) (((long) size + 63) / 64)];
    }

    public Builder set(int index) {
      checkIndex(index);
      bitmap[index >>> 6] |= 1L << (index & 63);
      return this;
    }

    public Builder clear(int index) {
      checkIndex(index);
      bitmap[index >>> 6] &= ~(1L << (index & 63));
      return this;
    }

    public RankBitmap build() {
      long[] bitmapCopy = Arrays.copyOf(bitmap, bitmap.length);
      int[] checkpoints = buildCheckpoints(bitmapCopy, size);
      return new RankBitmap(size, bitmapCopy, checkpoints);
    }

    private void checkIndex(int index) {
      if (index < 0 || index >= size) {
        throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
      }
    }
  }
}
