package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;
import lombok.experimental.PackagePrivate;
import lombok.experimental.SuperBuilder;

/**
 * An RNA synthesized on a DNA or RNA template by an RNA polymerase.
 *
 * @see <a href="http://sequenceontology.org/browser/current_release/term/SO:0000673">SO:0000673</a>
 */
@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Transcript extends ClosedInterval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull String id;
  @PackagePrivate int geneIndex;
  @NonNull Exon[] exons;
  Cds cds;
}
