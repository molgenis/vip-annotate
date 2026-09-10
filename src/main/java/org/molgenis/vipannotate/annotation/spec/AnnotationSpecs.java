package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;
import java.util.function.BiConsumer;

public record AnnotationSpecs(Map<String, AnnotationSpec> annotationSpecMap) {
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public AnnotationSpecs {}

  public int size() {
    return annotationSpecMap.size();
  }

  public void forEach(BiConsumer<? super String, ? super AnnotationSpec> action) {
    annotationSpecMap.forEach(action);
  }
}
