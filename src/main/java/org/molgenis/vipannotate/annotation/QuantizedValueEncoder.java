package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public final class QuantizedValueEncoder implements DoubleToIntValueEncoder {
  private final Quantizer quantizer;

  @Override
  public int encode(double value) {
    return quantizer.quantize(value);
  }
}
