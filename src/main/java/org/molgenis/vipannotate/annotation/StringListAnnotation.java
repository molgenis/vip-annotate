package org.molgenis.vipannotate.annotation;

public record StringListAnnotation(String[] values) implements Annotation {
  @Override
  public boolean isMissingValue() {
    return values.length == 0;
  }
}
