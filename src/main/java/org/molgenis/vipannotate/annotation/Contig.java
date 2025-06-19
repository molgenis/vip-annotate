package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.requireNonNegative;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Contig {
  private final String name;
  private final int length;

  public Contig(@NonNull String name, int length) {
    this.name = name;
    this.length = requireNonNegative(length);
  }
}
