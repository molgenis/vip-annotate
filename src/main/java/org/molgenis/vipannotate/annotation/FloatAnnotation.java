package org.molgenis.vipannotate.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public final class FloatAnnotation implements ScalarAnnotation {
  private boolean isNull;
  @Getter private double value;

  public FloatAnnotation() {
    this(true, 0.0d);
  }

  public FloatAnnotation(double value) {
    this(false, value);
  }

  @Override
  public boolean isMissingValue() {
    return isNull;
  }

  @Override
  public void reset() {
    this.isNull = true;
    this.value = 0.0d;
  }

  public void reset(double value) {
    this.isNull = false;
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FloatAnnotation other)) return false;

    if (this.isNull && other.isNull) return true;
    if (this.isNull != other.isNull) return false;

    return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.getValue());
  }

  @Override
  public int hashCode() {
    if (isNull) return 0;
    return Long.hashCode(Double.doubleToLongBits(value));
  }
}
