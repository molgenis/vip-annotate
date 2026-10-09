package org.molgenis.vipannotate.annotation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class StringListAnnotation implements Annotation {
  private String[] values;

  @Override
  public boolean isMissingValue() {
    return values.length == 0;
  }

  public void reset(String[] values) {
    this.values = values;
  }
}
