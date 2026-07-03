package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public final class QuantizedValueDecoder implements U16ToF64ValueDecoder {
  private final Quantizer quantizer;

  @Override
  public double decode(int value) {
    return quantizer.dequantize(value);
  }
}
