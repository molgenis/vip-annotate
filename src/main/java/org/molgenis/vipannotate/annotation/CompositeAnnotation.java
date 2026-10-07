package org.molgenis.vipannotate.annotation;

public record CompositeAnnotation(Annotation[] annotations) implements Annotation {
  @Override
  public boolean isMissingValue() {
    for (Annotation annotation : annotations) {
      if (!annotation.isMissingValue()) {
        return false;
      }
    }

    return true;
  }
}
