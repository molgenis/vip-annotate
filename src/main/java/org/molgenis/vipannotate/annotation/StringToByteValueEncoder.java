package org.molgenis.vipannotate.annotation;

public sealed interface StringToByteValueEncoder extends ValueEncoder
    permits EnumStringToByteValueEncoder {
  byte encode(String value);
}
