package org.molgenis.vcf.annotate.db.utils;

import java.io.Serializable;
import lombok.Getter;

/**
 * Derived from <a
 * href="https://github.com/apache/lucene/blob/releases/lucene/9.11.1/lucene/core/src/java/org/apache/lucene/util/FixedBitSet.java">https://github.com/apache/lucene/blob/releases/lucene/9.11.1/lucene/core/src/java/org/apache/lucene/util/FixedBitSet.java</a>
 */
@Getter
public class BitList implements Serializable {
  private final long[] bits;
  private final int nrBits;

  public BitList(int numBits) {
    this.nrBits = numBits;
    this.bits = new long[((numBits - 1) >> 6) + 1];
  }

  public boolean get(int index) {
    int i = index >> 6;
    long bitmask = 1L << index;
    return (bits[i] & bitmask) != 0;
  }

  public void set(int index) {
    int wordNum = index >> 6;
    long bitmask = 1L << index;
    bits[wordNum] |= bitmask;
  }
}
