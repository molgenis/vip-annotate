package org.molgenis.vcf.annotate.db.model;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
public class Interval {
  int start;

  int length;

  public int getStop() {
    return start + length;
  }
}
