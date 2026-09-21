package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.function.BiConsumer;

public record AnnotationAnalyses(Map<String, AnnotationAnalysis> annotationAnalyses) {
  public int size() {
    return annotationAnalyses.size();
  }

  public void forEach(BiConsumer<? super String, ? super AnnotationAnalysis> action) {
    annotationAnalyses.forEach(action);
  }
}
