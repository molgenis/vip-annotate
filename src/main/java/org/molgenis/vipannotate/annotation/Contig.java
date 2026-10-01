package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.requireNonNegative;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Contig {
  private final String name;
  // FIXME long?
  private final int length;

  public Contig(String name, int length) {
    this.name = name;
    this.length = requireNonNegative(length);
  }
}
