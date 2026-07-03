package org.molgenis.vipannotate.annotation;

public sealed interface DoubleToIntValueEncoder extends ValueEncoder permits QuantizedValueEncoder {
  int encode(double value);
}
