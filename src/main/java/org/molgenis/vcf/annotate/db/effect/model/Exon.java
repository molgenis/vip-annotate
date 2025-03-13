package org.molgenis.vcf.annotate.db.effect.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * A region of the transcript sequence within a gene which is not removed from the primary RNA
 * transcript by RNA splicing.
 *
 * @see <a
 *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000147">SO:0000147</a>
 */
@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Exon extends ClosedInterval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
