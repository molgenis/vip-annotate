package org.molgenis.vipannotate.annotation;

public record CompositeAnnotation(Annotation[] annotations) implements Annotation {
  @Override
  public boolean isMissingValue() {
    if (annotations.length == 0) {
      return true;
    }

    for (Annotation annotation : annotations) {
      if (annotation.isMissingValue()) {
        return true;
      }
    }

    return false;
  }
}
