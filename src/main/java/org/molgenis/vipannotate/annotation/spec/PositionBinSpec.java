package org.molgenis.vipannotate.annotation.spec;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;
import static org.molgenis.vipannotate.util.Numbers.validatePositive;

import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

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

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeLong(offset);
    binaryWriter.writeLong(length);
    binaryWriter.writeByte(bits);
  }

  public static PositionBinSpec readFrom(BinaryReader binaryReader) {
    long offset = binaryReader.readLong();
    long length = binaryReader.readLong();
    byte bits = binaryReader.getByte();
    return new PositionBinSpec(offset, length, bits);
  }
}
