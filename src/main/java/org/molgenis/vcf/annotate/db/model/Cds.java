package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * A contiguous sequence which begins with, and includes, a start codon and ends with, and includes,
 * a stop codon.
 *
 * @see <a
 *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000316">SO:0000316</a>
 */
@Builder
public record Cds(@NonNull String proteinId, Part[] parts) implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Value
  @EqualsAndHashCode(callSuper = true)
  @SuperBuilder
  public static class Part extends ClosedInterval implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    byte phase;
  }
}
