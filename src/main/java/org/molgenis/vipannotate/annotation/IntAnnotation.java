package org.molgenis.vipannotate.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public final class IntAnnotation implements ScalarAnnotation {
  private boolean isNull;
  @Getter private long value;

  public IntAnnotation() {
    this(true, 0L);
  }

  public IntAnnotation(long value) {
    this(false, value);
  }

  @Override
  public boolean isMissingValue() {
    return isNull;
  }

  @Override
  public void reset() {
    this.isNull = true;
    this.value = 0;
  }

  public void reset(long value) {
    this.isNull = false;
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IntAnnotation other)) return false;

    if (this.isNull && other.isNull) return true;
    if (this.isNull != other.isNull) return false;

    return this.value == other.getValue();
  }

  @Override
  public int hashCode() {
    if (isNull) return 0;
    return Long.hashCode(value);
  }
}
