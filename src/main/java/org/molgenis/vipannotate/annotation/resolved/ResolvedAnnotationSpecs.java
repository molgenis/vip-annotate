package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.function.BiConsumer;

public record ResolvedAnnotationSpecs(
    @JsonValue Map<String, ResolvedAnnotationSpec> annotationSpecMap) {
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public ResolvedAnnotationSpecs {}

  public int size() {
    return annotationSpecMap.size();
  }

  public void forEach(BiConsumer<? super String, ? super ResolvedAnnotationSpec> action) {
    annotationSpecMap.forEach(action);
  }
}
