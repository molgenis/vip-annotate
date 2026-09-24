package org.molgenis.vipannotate.annotation.resolved;

import java.util.Map;
import java.util.function.BiConsumer;

public record ResolvedAnnotationSpecs(Map<String, ResolvedAnnotationSpec> annotationSpecMap) {
  public int size() {
    return annotationSpecMap.size();
  }

  public void forEach(BiConsumer<? super String, ? super ResolvedAnnotationSpec> action) {
    annotationSpecMap.forEach(action);
  }
}
