package org.molgenis.vipannotate.annotation;

import lombok.*;

public sealed interface ScalarAnnotation extends Annotation {
  @Getter
  @AllArgsConstructor
  @ToString
  @EqualsAndHashCode
  final class DoubleAnnotation implements ScalarAnnotation {
    private double value;

    public void reset(double value) {
      this.value = value;
    }
  }

  @AllArgsConstructor
  @ToString
  final class NullableDoubleAnnotation implements ScalarAnnotation {
    @Getter private boolean isNull;
    @Getter private double value;

    public NullableDoubleAnnotation() {
      this(true, Double.NaN);
    }

    public NullableDoubleAnnotation(double value) {
      this(false, value);
    }

    public void reset() {
      this.isNull = true;
      this.value = Double.NaN;
    }

    public void reset(double value) {
      this.isNull = false;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof NullableDoubleAnnotation other)) return false;

      if (this.isNull && other.isNull()) return true;
      if (this.isNull != other.isNull()) return false;

      return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.getValue());
    }

    @Override
    public int hashCode() {
      if (isNull) return 0;
      return Long.hashCode(Double.doubleToLongBits(value));
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @ToString
  @EqualsAndHashCode
  final class IntAnnotation implements ScalarAnnotation {
    private int value;
  }

  @AllArgsConstructor
  @ToString
  final class NullableIntAnnotation implements ScalarAnnotation {
    @Getter private boolean isNull;
    @Getter private int value;

    public NullableIntAnnotation() {
      this(true, 0);
    }

    public NullableIntAnnotation(int value) {
      this(false, value);
    }

    public void reset() {
      this.isNull = true;
      this.value = 0; // FIXME can't do NaN here, does it work correctly?
    }

    public void reset(int value) {
      this.isNull = false;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof NullableIntAnnotation other)) return false;

      if (this.isNull && other.isNull()) return true;
      if (this.isNull != other.isNull()) return false;

      return this.value == other.getValue();
    }

    @Override
    public int hashCode() {
      if (isNull) return 0;
      return Integer.hashCode(value);
    }
  }
}
