package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public record StringAnnotation(@Nullable String value) implements Annotation {
  @Override
  public boolean isMissingValue() {
    return value == null;
  }
}
