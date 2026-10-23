package org.molgenis.vipannotate.annotation.spec;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;
import static org.molgenis.vipannotate.util.Numbers.validatePositive;

import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

/**
 * Position bin specification.
 *
 * @param offset first position in the bin (0-based, inclusive)
 * @param length number of positions in the range
 * @param bits bin contains {@code 2^bits} positions
 */
public record PositionBinSpec(long offset, long length, byte bits) {
  public PositionBinSpec {
    validateNonNegative(offset);
    validatePositive(length);
    validatePositive(bits);
  }

  public long endExclusive() {
    return offset + length;
  }

  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putLong(offset);
    memEncoder.putLong(length);
    memEncoder.putByte(bits);
  }

  public static PositionBinSpec readFrom(MemoryDecoder memDecoder) {
    long offset = memDecoder.getLong();
    long length = memDecoder.getLong();
    byte bits = memDecoder.getByte();
    return new PositionBinSpec(offset, length, bits);
  }
}
