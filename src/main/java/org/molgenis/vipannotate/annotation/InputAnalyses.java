package org.molgenis.vipannotate.annotation;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiConsumer;

public record InputAnalyses(
    EnumSet<SequenceVariantType> sequenceVariantTypes,
    Map<String, FieldAnalysis> annotationAnalyses) {
  public int size() {
    return annotationAnalyses.size();
  }

  public void forEach(BiConsumer<? super String, ? super FieldAnalysis> action) {
    annotationAnalyses.forEach(action);
  }
}
