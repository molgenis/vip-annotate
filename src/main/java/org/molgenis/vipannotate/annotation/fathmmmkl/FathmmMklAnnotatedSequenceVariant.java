package org.molgenis.vipannotate.annotation.fathmmmkl;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.molgenis.vipannotate.annotation.*;

/**
 * sequence variant annotated with FATHMM-MKL
 *
 * @see <a href="https://doi.org/10.1093/bioinformatics/btv009">FATHMM-MKL</a>
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FathmmMklAnnotatedSequenceVariant
    extends AnnotatedSequenceVariant<FathmmMklAnnotation> {
  public FathmmMklAnnotatedSequenceVariant(SequenceVariant variant, FathmmMklAnnotation annotation) {
    super(variant, annotation);
  }
}
