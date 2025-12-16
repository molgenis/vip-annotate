package org.molgenis.vipannotate.annotation.spliceai;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.molgenis.vipannotate.annotation.*;

/**
 * sequence variant annotated with SpliceAI
 *
 * @see <a href="https://doi.org/10.1016/j.cell.2018.12.015">SpliceAI</a>
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SpliceAiAnnotatedSequenceVariant extends AnnotatedSequenceVariant<SpliceAiAnnotation> {
  public SpliceAiAnnotatedSequenceVariant(SequenceVariant variant, SpliceAiAnnotation annotation) {
    super(variant, annotation);
  }
}
