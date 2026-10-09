package org.molgenis.vipannotate.annotation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class StringAnnotation implements Annotation {
  private @Nullable String value;

  @Override
  public boolean isMissingValue() {
    return value == null;
  }

  public void reset(@Nullable String value) {
    this.value = value;
  }
}
