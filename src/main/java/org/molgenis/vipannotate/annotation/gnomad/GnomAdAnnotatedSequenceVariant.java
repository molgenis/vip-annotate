package org.molgenis.vipannotate.annotation.gnomad;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.molgenis.vipannotate.annotation.*;

/**
 * sequence variant annotated with gnomAD
 *
 * @see <a href="https://gnomad.broadinstitute.org/">gnomAD</a>
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GnomAdAnnotatedSequenceVariant extends AnnotatedSequenceVariant<GnomAdAnnotation> {
  public GnomAdAnnotatedSequenceVariant(SequenceVariant variant, GnomAdAnnotation annotation) {
    super(variant, annotation);
  }
}
