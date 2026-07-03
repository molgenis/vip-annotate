package org.molgenis.vipannotate.annotation;

public sealed interface U16ToF64ValueDecoder extends ValueDecoder permits QuantizedValueDecoder {
  double decode(int value);
}
