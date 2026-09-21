package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.BedInputFormat;
import org.molgenis.vipannotate.util.Input;

@RequiredArgsConstructor
public class BedAnnotationsAnalyzer implements AnnotationsAnalyzer {
  private final BedInputFormat bedInputFormat;

  @Override
  public AnnotationAnalyses analyze(Input input, AnnotationSpecs annotationSpecs) {
    throw new UnsupportedOperationException("not implemented yet"); // FIXME implement
  }
}
