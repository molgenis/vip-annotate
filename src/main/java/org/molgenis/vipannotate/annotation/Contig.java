package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.requireNonNegativeOrNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Contig {
  private final String name;
  private final Integer length;

  public Contig(@NonNull String name) {
    this(name, null);
  }

  public Contig(@NonNull String name, Integer length) {
    this.name = name;
    this.length = requireNonNegativeOrNull(length);
  }
}
