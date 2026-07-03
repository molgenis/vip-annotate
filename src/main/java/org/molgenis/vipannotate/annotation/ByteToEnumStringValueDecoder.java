package org.molgenis.vipannotate.annotation;

public final class ByteToEnumStringValueDecoder implements ByteToStringValueDecoder {
  private final String[] values;
  private final byte[] encodedValues;

  public ByteToEnumStringValueDecoder(String[] values, byte[] encodedValues) {
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
