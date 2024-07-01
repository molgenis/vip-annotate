package org.molgenis.vipannotate.annotation.remm;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.molgenis.vipannotate.annotation.AnnotatedPosition;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;

/**
 * genomic position annotated with Regulatory Mendelian Mutation (ReMM) score
 *
 * @see <a href="https://doi.org/10.1093/gigascience/giad024">article</a>
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RemmAnnotatedPosition extends AnnotatedPosition<DoubleValueAnnotation> {
  public RemmAnnotatedPosition(Position genomicFeature, DoubleValueAnnotation annotation) {
    super(genomicFeature, annotation);
  }
}
