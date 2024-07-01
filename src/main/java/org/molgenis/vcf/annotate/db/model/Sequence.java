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
public class Sequence extends Interval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull SequenceType sequenceType;
  long @NonNull [] bits;

  public byte[] get(int absStart, int absStop) {
    int start = absStart - getStart();
    int stop = absStop - getStart();

    return switch (sequenceType) {
      case ACTG -> getActg(start, stop);
      case ACTGN -> getActgn(start, stop);
    };
  }

  private byte[] getActg(int start, int stop) {
    byte[] sequence = new byte[stop - start + 1];
    for (int bitIndex = start * 2, byteIndex = 0;
        bitIndex < (stop + 1) * 2;
        bitIndex += 2, byteIndex++) {
      if (get(bitIndex)) { // 1?
        if (get(bitIndex + 1)) { // 11
          sequence[byteIndex] = 'G';
        } else { // 10
          sequence[byteIndex] = 'T';
        }
      } else { // 0?
        if (get(bitIndex + 1)) { // 01
          sequence[byteIndex] = 'C';
        } else { // 00
          sequence[byteIndex] = 'A';
        }
      }
    }
    return sequence;
  }

  private byte[] getActgn(int start, int stop) {
    byte[] sequence = new byte[stop - start + 1];
    for (int bitIndex = start * 3, byteIndex = 0;
        bitIndex < (stop + 1) * 3;
        bitIndex += 3, byteIndex++) {
      if (get(bitIndex)) { // 1??
        if (get(bitIndex + 1)) { // 11?
          throw new IllegalStateException();
        } else { // 10?
          if (get(bitIndex + 2)) { // 101
            throw new IllegalStateException();
          } else { // 100
            sequence[byteIndex] = 'N';
          }
        }
      } else { // 0??
        if (get(bitIndex + 1)) { // 01?
          if (get(bitIndex + 2)) { // 011
            sequence[byteIndex] = 'G';
          } else { // 010
            sequence[byteIndex] = 'T';
          }
        } else { // 00?
          if (get(bitIndex + 2)) { // 001
            sequence[byteIndex] = 'C';
          } else { // 000
            sequence[byteIndex] = 'A';
          }
        }
      }
    }
    return sequence;
  }

  private boolean get(int index) {
    int i = index >> 6;
    long bitmask = 1L << index;
    return (bits[i] & bitmask) != 0;
  }
}
