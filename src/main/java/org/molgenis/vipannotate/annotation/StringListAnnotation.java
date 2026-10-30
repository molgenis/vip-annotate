package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class StringListAnnotation implements Annotation {
  private final List<String> values;

  public StringListAnnotation() {
    values = new ArrayList<>(2);
  }

  @Override
  public boolean isMissingValue() {
    return values.isEmpty();
  }

  @Override
  public void reset() {
    this.values.clear();
  }

  public void add(String value) {
    this.values.add(value);
  }
}
