package org.molgenis.vcf.annotate.db.effect.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * A region (or regions) that includes all of the sequence elements necessary to encode a functional
 * transcript. A gene may include regulatory regions, transcribed regions and/or other functional
 * sequence regions.
 *
 * @see <a
 *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000704">SO:0000704</a>
 */
@Value
@Builder
public class Gene {
  int id;
  @NonNull String name;
  @NonNull Strand strand;
}
