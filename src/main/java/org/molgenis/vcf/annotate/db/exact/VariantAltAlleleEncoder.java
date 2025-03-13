package org.molgenis.vcf.annotate.db.exact;

import java.math.BigInteger;
import java.util.BitSet;
import org.apache.fury.memory.MemoryBuffer;

/**
 * Based on <a href="https://doi.org/10.1093/nar/gkac931">Echtvar: compressed variant representation
 * for rapid annotation and filtering of SNPs and indels</a>.
 */
public class VariantAltAlleleEncoder {
  public VariantAltAlleleEncoder() {}

  public int getPartitionId(VariantAltAllele variantAltAllele) {
    return variantAltAllele.start() >> 20;
  }

  public static boolean isSmallVariant(VariantAltAllele variantAltAllele) {
    return isSmall(variantAltAllele.getRefAlleleLength())
        && isSmall(variantAltAllele.getAltAlleleLength());
  }

  private static boolean isSmall(int nrBases) {
    if (nrBases == 0) {
      throw new IllegalArgumentException("number of ref bases must be > 0");
    }
    return nrBases <= 4;
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
  public static int encodeSmall(VariantAltAllele variantAltAllele) {
    int pos = variantAltAllele.start();
    byte[] altBases = variantAltAllele.bases();

    int encodedPos = encodePos(pos);
    int encodedRefLength = encodeSmallNrBases(variantAltAllele.getRefAlleleLength());
    int encodedAltLength = encodeSmallNrBases(variantAltAllele.getAltAlleleLength());
    int encodedAlt;
    try {
      encodedAlt = encodeSmallAlt(altBases);
    } catch (IllegalArgumentException e) {
      System.out.println(variantAltAllele);
      throw e;
    }

    return encodedPos << 12 | encodedRefLength << 10 | encodedAltLength << 8 | encodedAlt;
  }

  /**
   * Returns an encoded variant for variants with > 4 reference bases or > 4 alternate bases
   *
   * @return encoded variant
   */
  public static BigInteger encodeBig(VariantAltAllele variantAltAllele) {
    int pos = variantAltAllele.start();
    int nrRefBases = variantAltAllele.stop() - variantAltAllele.start() + 1;
    byte[] altBases = variantAltAllele.bases();

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
    if (pos < 1) throw new IllegalArgumentException("pos must be greater than or equal to 1");
    int binIndex = pos >> 20;
    return pos - (binIndex << 20);
  }

  private static void validateSmall(int nrBases) {
    if (!isSmall(nrBases)) {
      throw new IllegalArgumentException("number of bases is outside of range [1,4]");
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
   * @param altBases [1,4] alternate bases (each base must be one of A,C,G,T)
   * @return alternate bases encoded in 8 bits
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
}
