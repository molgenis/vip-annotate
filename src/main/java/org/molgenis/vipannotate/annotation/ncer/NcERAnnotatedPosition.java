package org.molgenis.vipannotate.annotation.ncer;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.molgenis.vipannotate.annotation.AnnotatedPosition;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;

/**
 * genomic position annotated with non-coding essential regulation (ncER) score
 *
 * @see <a href="https://doi.org/10.1038/s41467-019-13212-3">article</a>
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class NcERAnnotatedPosition extends AnnotatedPosition<DoubleValueAnnotation> {
  public NcERAnnotatedPosition(Position genomicFeature, DoubleValueAnnotation annotation) {
    super(genomicFeature, annotation);
  }
}
