package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.VcfInputFormat;
import org.molgenis.vipannotate.util.Input;

@RequiredArgsConstructor
public class VcfAnnotationsAnalyzer implements AnnotationsAnalyzer {
  private final VcfInputFormat vcfInputFormat;

  @Override
  public AnnotationAnalyses analyze(Input input, AnnotationSpecs annotationSpecs) {
    throw new UnsupportedOperationException("not implemented yet"); // FIXME implement
  }
}
