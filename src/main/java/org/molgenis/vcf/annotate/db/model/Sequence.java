package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/** nucleotide sequence encoded for space-efficiency and fast lookup */
@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Sequence extends ClosedInterval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull SequenceType sequenceType;
  long @NonNull [] bits;

  /**
   * @return sequence in [start, stop] if start <= stop, or the reverse sequence in [stop, start]
   */
  public char[] get(int start, int stop, Strand strand) {
    start = start - getStart();
    stop = stop - getStart();

    char[] nucs;
    switch (strand) {
      case POSITIVE -> {
        nucs = new char[stop - start + 1];
        for (int i = start, idx = 0; i <= stop; i++, idx++) {
          nucs[idx] = getNuc(i, strand);
        }
      }
      case NEGATIVE -> {
        nucs = new char[start - stop + 1];
        for (int i = start, idx = 0; i >= stop; i--, idx++) {
          nucs[idx] = getNuc(i, strand);
        }
      }
      case null -> throw new RuntimeException();
    }
    return nucs;
  }

  private char getNuc(int relativePos, Strand strand) {
    return switch (sequenceType) {
      case ACTG -> getActg(relativePos, strand);
      case ACTGN -> getActgn(relativePos, strand);
    };
  }

  private char getActg(int relativePos, Strand strand) {
    int bitIndex = relativePos * 2;

    char nuc;
    if (get(bitIndex)) {
      if (get(bitIndex + 1)) {
        nuc = strand == Strand.POSITIVE ? 'G' : 'C'; // 11
      } else {
        nuc = strand == Strand.POSITIVE ? 'T' : 'A'; // 10
      }
    } else {
      if (get(bitIndex + 1)) {
        nuc = strand == Strand.POSITIVE ? 'C' : 'G'; // 01
      } else {
        nuc = strand == Strand.POSITIVE ? 'A' : 'T'; // 00
      }
    }
    return nuc;
  }

  private char getActgn(int relativePos, Strand strand) {
    int bitIndex = relativePos * 3;

    char nuc;
    if (get(bitIndex)) {
      if (get(bitIndex + 1)) {
        throw new IllegalStateException(); // 111 or 110
      } else {
        if (get(bitIndex + 2)) {
          throw new IllegalStateException(); // 101
        } else {
          nuc = 'N'; // 100
        }
      }
    } else {
      if (get(bitIndex + 1)) {
        if (get(bitIndex + 2)) {
          nuc = strand == Strand.POSITIVE ? 'G' : 'C'; // 011
        } else {
          nuc = strand == Strand.POSITIVE ? 'T' : 'A'; // 010
        }
      } else {
        if (get(bitIndex + 2)) {
          nuc = strand == Strand.POSITIVE ? 'C' : 'G'; // 001
        } else {
          nuc = strand == Strand.POSITIVE ? 'A' : 'T'; // 000
        }
      }
    }
    return nuc;
  }

  private boolean get(int index) {
    int i = index >> 6;
    long bitmask = 1L << index;
    return (bits[i] & bitmask) != 0;
  }
}
