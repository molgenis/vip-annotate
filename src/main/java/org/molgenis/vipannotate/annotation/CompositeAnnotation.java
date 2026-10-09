package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

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

  public Annotation annotation(int index) {
    return annotations[index];
  }

  public void reset(int index, @Nullable Annotation annotation) {
    annotations[index] = annotation;
  }
}
