package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.AlleleUtils;

/**
 * Based on <a href="https://doi.org/10.1093/nar/gkac931">Echtvar: compressed variant representation
 * for rapid annotation and filtering of SNPs and indels</a>.
 */
public class SequenceVariantEncoder {
  private SequenceVariantEncoder() {}

  public static boolean isSmallVariant(SequenceVariant variant) {
    return switch (variant.getType()) {
      case SNV, MNV, INDEL, INSERTION, DELETION -> {
        String alt = variant.getAlt().alt();
        yield alt != null
            && alt.length() <= 4
            && AlleleUtils.isActg(alt)
            && variant.getRefLength() <= 16;
      }
      case STRUCTURAL, OTHER -> false;
    };
  }

  /**
   * Returns an encoded variant for variants with [1,16] reference bases and [1,4] alternate bases
   *
   * <ul>
   *   <li>{@code 18 bits} encoded position
   *   <li>{@code 04 bits} encoded ref length
   *   <li>{@code 02 bits} encoded alt length
   *   <li>{@code 08 bits} encoded alt
   * </ul>
   *
   * @return encoded variant
   */
  public static int encodeSmall(SequenceVariant variant) {
    byte[] altBases = requireNonNull(variant.getAlt().alt()).getBytes(StandardCharsets.UTF_8);

    int encodedPos = encodePos(variant.getStart());
    int encodedRefLength = encodeNrBases(variant.getRefLength());
    int encodedAltLength = encodeNrBases(altBases.length);
    int encodedAlt = encodeSmallAlt(altBases);
    return encodedPos << 14 | encodedRefLength << 10 | encodedAltLength << 8 | encodedAlt;
  }

  /**
   * Returns an encoded variant for variants with > 4 reference bases or > 4 alternate bases
   *
   * @return encoded variant
   */
  public static BigInteger encodeBig(SequenceVariant variant) {
    String alt = variant.getAlt().alt();
    byte[] altBases = alt != null ? alt.getBytes(StandardCharsets.UTF_8) : new byte[0];

    int encodedPos = encodePos(variant.getStart());
    int encodedRefLength = encodeNrBases(variant.getRefLength());
    byte[] encodedAlt = encodeBigAlt(altBases);

    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(32);
    memoryBuffer.writeVarUint32(encodedPos);
    memoryBuffer.writeVarUint32(encodedRefLength);
    memoryBuffer.writeBytes(encodedAlt);

    return new BigInteger(memoryBuffer.getHeapMemory(), 0, memoryBuffer.writerIndex());
  }

  /** encodes number of bases as zero-based number */
  private static int encodeNrBases(int nrBases) {
    return nrBases - 1;
  }

  /**
   * @param pos position >= 1
   * @return position encoded in 18 bits
   */
  private static int encodePos(int pos) { // TODO make zero-based
    if (pos < 1) throw new IllegalArgumentException("start must be greater than or equal to 1");
    return Partition.calcPosInBin(pos);
  }

  /**
   * encoding:
   *
   * <ul>
   *   <li>{@code 2 bits} = alternate base 4
   *   <li>{@code 2 bits} = alternate base 3
   *   <li>{@code 2 bits} = alternate base 2
   *   <li>{@code 2 bits} = alternate base 1
   * </ul>
   *
   * @param altBases [1,4] alternate bases (each base must be one of [A, C, G, T])
   * @return alternate alt encoded in 8 bits
   */
  private static int encodeSmallAlt(byte[] altBases) {

    int encodedAlt = 0;
    for (int i = 0; i < altBases.length; i++) {
      int encodedAltBase = encodeAltBase(altBases[i]);
      encodedAlt |= encodedAltBase << (i * 2);
    }
    return encodedAlt;
  }

  private static byte[] encodeBigAlt(byte[] altBases) {
    int nrBits = altBases.length * 2;
    BitSet bitSet = new BitSet(nrBits);

    int pos = 0;
    for (byte altBase : altBases) {
      int enc = encodeAltBase(altBase);

      if ((enc >> 1 & 1) == 1) {
        bitSet.set(pos);
      }
      if ((enc & 1) == 1) {
        bitSet.set(pos + 1);
      }

      pos += 2;
    }
    return bitSet.toByteArray();
  }

  private static int encodeAltBase(byte altBase) {
    return switch (altBase) {
      case 'A' -> 0;
      case 'C' -> 1;
      case 'G' -> 2;
      case 'T' -> 3;
      default ->
          throw new IllegalArgumentException(
              "alt base '%s' not allowed, must be one of A,C,G,T".formatted((char) altBase));
    };
  }
}
