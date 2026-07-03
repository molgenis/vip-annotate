package org.molgenis.vipannotate.annotation;

public sealed interface ByteToStringValueDecoder extends ValueDecoder
    permits ByteToEnumStringValueDecoder {
  byte encode(String value);
}
