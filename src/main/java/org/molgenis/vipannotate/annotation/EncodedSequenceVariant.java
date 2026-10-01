package org.molgenis.vipannotate.annotation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EncodedSequenceVariant {
  @Getter private Type type;
  private int small;
  private byte @Nullable [] big;
  private int bigLength;

  public static EncodedSequenceVariant createSmall(int small) {
    return new EncodedSequenceVariant(Type.POS_20_BIT, small, null, -1);
  }

  public void resetSmall(int small) {
    this.type = Type.POS_20_BIT;
    this.small = small;
    this.big = null;
  }

  public static EncodedSequenceVariant createBig(byte[] bytes) {
    return new EncodedSequenceVariant(Type.BIG, -1, bytes, bytes.length);
  }

  public static EncodedSequenceVariant createBig(byte[] bytes, int length) {
    return new EncodedSequenceVariant(Type.BIG, -1, bytes, length);
  }

  public void resetBig(byte[] bytes) {
    resetBig(bytes, bytes.length);
  }

  public void resetBig(byte[] bytes, int length) {
    this.type = Type.BIG;
    this.small = -1;
    this.big = bytes;
    this.bigLength = length;
  }

  public int getSmall() {
    if (type != Type.POS_20_BIT) {
      throw new IllegalStateException("variant is not small");
    }
    return small;
  }

  @SuppressWarnings("NullAway")
  public byte[] getBigBytes() {
    if (type != Type.BIG) {
      throw new IllegalStateException("variant is not big");
    }
    return big;
  }

  public int getBigBytesLength() {
    if (type != Type.BIG) {
      throw new IllegalStateException("variant is not big");
    }
    return bigLength;
  }

  public enum Type {
    POS_20_BIT,
    POS_26_BIT,
    BIG,
    OTHER
  }
}
