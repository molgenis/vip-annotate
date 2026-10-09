package org.molgenis.vipannotate.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class IndexRange {
  private int start;
  private int end;

  public IndexRange(int start, int end) {
    validate(start, end);
    this.start = start;
    this.end = end;
  }

  public void reset(int start, int end) {
    validate(start, end);
    this.start = start;
    this.end = end;
  }

  private static void validate(int start, int end) {
    if (start < 0 || end < start) {
      throw new IllegalArgumentException("Invalid index range: [%d, %d]".formatted(start, end));
    }
  }
}
