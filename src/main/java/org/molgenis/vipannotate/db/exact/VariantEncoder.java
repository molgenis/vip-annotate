package org.molgenis.vipannotate.db.exact;

import java.math.BigInteger;
import java.util.BitSet;
import org.apache.fury.memory.MemoryBuffer;

/**
 * Based on <a href="https://doi.org/10.1093/nar/gkac931">Echtvar: compressed variant representation
 * for rapid annotation and filtering of SNPs and indels</a>.
 */
public class VariantEncoder {
  private static final int NR_ENCODED_PARTITION_ID_BITS = 20;

  public VariantEncoder() {}

  public int getPartitionId(Variant variant) {
    return variant.start() >> NR_ENCODED_PARTITION_ID_BITS;
  }

  public static boolean isSmallVariant(Variant variant) {
    return isSmall(variant.getRefLength()) && isSmall(variant.getAltLength());
  }

  private static boolean isSmall(int nrBases) {
    if (nrBases == 0) {
      throw new IllegalArgumentException("number of ref alt must be > 0");
    }
    return nrBases <= 6;
  }

  /**
   * Returns an encoded variant for variants with [1,4] reference bases and [1,4] alternate bases
   *
   * <ul>
   *   <li>{@code 20 bits} encoded position
   *   <li>{@code 02 bits} encoded ref length
   *   <li>{@code 02 bits} encoded alt length
   *   <li>{@code 08 bits} encoded alt
   * </ul>
   *
   * @return encoded variant
   */
  public static int encodeSmall(Variant variant) {
    int pos = variant.start();
    byte[] altBases = variant.alt();

    int encodedPos = encodePos(pos);
    int encodedRefLength = encodeSmallNrBases(variant.getRefLength());
    int encodedAltLength = encodeSmallNrBases(variant.getAltLength());
    int encodedAlt;
    try {
      encodedAlt = encodeSmallAlt(altBases);
    } catch (IllegalArgumentException e) {
      System.out.println(variant);
      throw e;
    }

    return encodedPos << 12 | encodedRefLength << 10 | encodedAltLength << 8 | encodedAlt;
  }

  /**
   * Returns an encoded variant for variants with > 4 reference alt or > 4 alternate alt
   *
   * @return encoded variant
   */
  public static BigInteger encodeBig(Variant variant) {
    int pos = variant.start();
    int nrRefBases = variant.stop() - variant.start() + 1;
    byte[] altBases = variant.alt();

    int encodedPos = encodePos(pos);
    byte[] encodedAlt = encodeBigAlt(altBases);

    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(32);
    memoryBuffer.writeVarUint32(encodedPos);
    memoryBuffer.writeVarUint32(nrRefBases);
    memoryBuffer.writeVarUint32(altBases.length);
    memoryBuffer.writeBytes(encodedAlt);

    return new BigInteger(memoryBuffer.getHeapMemory(), 0, memoryBuffer.writerIndex());
  }

  /**
   * encoding:
   *
   * <ul>
   *   <li>{@code 00} = 1 reference base
   *   <li>{@code 01} = 2 reference bases
   *   <li>{@code 10} = 3 reference bases
   *   <li>{@code 11} = 4 reference bases
   * </ul>
   *
   * @param nrBases number of bases in the range of {@code [1,4]}
   * @return number of bases encoded in two bits
   */
  private static int encodeSmallNrBases(int nrBases) {
    validateSmall(nrBases);
    return nrBases - 1;
  }

  /**
   * @param pos position >= 1
   * @return position encoded in 20 bits
   */
  private static int encodePos(int pos) {
    if (pos < 1) throw new IllegalArgumentException("start must be greater than or equal to 1");
    int binIndex = pos >> NR_ENCODED_PARTITION_ID_BITS;
    return pos - (binIndex << NR_ENCODED_PARTITION_ID_BITS);
  }

  private static void validateSmall(int nrBases) {
    if (!isSmall(nrBases)) {
      throw new IllegalArgumentException("number of alt is outside of range [1,4]");
    }
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
   * @param altBases [1,4] alternate alt (each base must be one of A,C,G,T)
   * @return alternate alt encoded in 8 bits
   */
  private static int encodeSmallAlt(byte[] altBases) {
    validateSmall(altBases.length);

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

  public static void main(String[] args) {
    int pos = (2 * 1048576) + 1242;
    int binIndex = pos >> 20;
    int relPos = pos - (binIndex << 20);
    System.out.println(relPos);
  }
}
