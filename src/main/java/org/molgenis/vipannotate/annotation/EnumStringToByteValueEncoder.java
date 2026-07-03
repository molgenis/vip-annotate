package org.molgenis.vipannotate.annotation;

public final class EnumStringToByteValueEncoder implements StringToByteValueEncoder {
  private final String[] values;
  private final byte[] encodedValues;

  public EnumStringToByteValueEncoder(String[] values, byte[] encodedValues) {
    if (values.length != encodedValues.length) throw new IllegalArgumentException();
    this.values = values;
    this.encodedValues = encodedValues;
  }

  @Override
  public byte encode(String value) {
    for (int i = 0, length = values.length; i < length; i++) {
      if (values[i].equals(value)) {
        return encodedValues[i];
      }
    }
    throw new IllegalArgumentException();
  }
}
