package org.molgenis.vcf.annotate.db.model;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
public class Interval {
  /** start in [start, stop) */
  int start;

  /** stop - start */
  int length;

  /** stop in [start, stop) */
  public int getStop() {
    return start + length;
  }
}
