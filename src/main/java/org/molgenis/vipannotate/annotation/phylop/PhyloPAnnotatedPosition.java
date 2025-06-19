package org.molgenis.vipannotate.annotation.phylop;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.molgenis.vipannotate.annotation.AnnotatedPosition;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;

/**
 * genomic position annotated with phylogenetic P-values (phyloP)
 *
 * @see <a href="https://doi.org/10.1101/gr.097857.109">article</a>
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PhyloPAnnotatedPosition extends AnnotatedPosition<DoubleValueAnnotation> {
  public PhyloPAnnotatedPosition(Position genomicFeature, DoubleValueAnnotation annotation) {
    super(genomicFeature, annotation);
  }
}
