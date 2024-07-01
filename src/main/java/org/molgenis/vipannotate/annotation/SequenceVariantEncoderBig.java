package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.serialization.MemoryBuffer.VAR_INT_MAX_BYTE_SIZE;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

public class SequenceVariantEncoderBig<T extends SequenceVariant>
    implements SequenceVariantEncoder<T> {
  @Override
  public EncodedSequenceVariant encode(T variant) {
    byte[] bytes = new byte[calcNrBytes(variant)];
    encodeIntoBytes(variant, bytes);
    return EncodedSequenceVariant.createBig(bytes);
  }

  @Override
  public void encodeInto(T variant, EncodedSequenceVariant encodedVariant) {
    int length = calcNrBytes(variant);
    byte[] bytes;
    if (encodedVariant.getBigBytesLength() < length) {
      bytes = encodedVariant.getBigBytes();
    } else {
      bytes = new byte[length];
    }
    encodeIntoBytes(variant, bytes);
    encodedVariant.resetBig(bytes, length);
  }

  private static void encodeIntoBytes(SequenceVariant variant, byte[] bytes) {
    CharSequence altBases = variant.getAlt().get(); // FIXME not true, can be symbolic etc.

    int encodedPos = SequenceVariantEncoderUtils.encodePos(variant.getStart());
    int encodedRefLength = SequenceVariantEncoderUtils.encodeNrBases(variant.getRefLength());
    int encodedAltLength = SequenceVariantEncoderUtils.encodeNrBases(altBases.length());

    try (MemoryBuffer memoryBuffer = MemoryBuffer.wrap(bytes)) {
      memoryBuffer.putVarUnsignedIntUnchecked(encodedPos);
      memoryBuffer.putVarUnsignedIntUnchecked(encodedRefLength);
      memoryBuffer.putVarUnsignedIntUnchecked(encodedAltLength);
      encodeAlt(altBases, bytes, Math.toIntExact(memoryBuffer.getPosition()));
    }
  }

  private static void encodeAlt(CharSequence altBases, byte[] dest, int offset) {
    int nrBases = altBases.length();
    int bitPos = 0;

    for (int i = 0; i < nrBases; i++) {
      int enc = SequenceVariantEncoderUtils.encodeAltBase(altBases.charAt(i)) & 0b11; // 2 bits
      int byteIndex = offset + (bitPos / 8);
      int bitOffset = bitPos % 8;

      dest[byteIndex] |= (byte) (dest[byteIndex] | (enc << bitOffset));

      bitPos += 2;
    }
  }

  private static int calcNrBytes(SequenceVariant variant) {
    CharSequence altBases = variant.getAlt().get();
    return (3 * VAR_INT_MAX_BYTE_SIZE) + (altBases.length() * 2);
  }
}
