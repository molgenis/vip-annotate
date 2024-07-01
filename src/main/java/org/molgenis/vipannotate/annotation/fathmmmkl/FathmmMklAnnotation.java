package org.molgenis.vipannotate.annotation.fathmmmkl;

import static org.molgenis.vipannotate.util.Numbers.*;

import org.molgenis.vipannotate.annotation.Annotation;

public record FathmmMklAnnotation(double score) implements Annotation {
  public FathmmMklAnnotation {
    validateUnitInterval(score);
  }
}
