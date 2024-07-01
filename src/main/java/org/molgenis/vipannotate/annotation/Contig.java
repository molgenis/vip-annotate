package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.requireNonNegativeOrNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

@Getter
@ToString
@EqualsAndHashCode
public class Contig {
  private final String name;
  @Nullable private final Integer length;

  public Contig(String name) {
    this(name, null);
  }

  public Contig(String name, @Nullable Integer length) {
    this.name = name;
    this.length = requireNonNegativeOrNull(length);
  }
}
