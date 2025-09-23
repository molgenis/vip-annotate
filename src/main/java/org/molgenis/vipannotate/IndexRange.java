package org.molgenis.vipannotate;

public record IndexRange(int start, int end) {
  public IndexRange {
    if (start < 0 || end < start) {
      throw new IllegalArgumentException("Invalid index range: [%d, %d]".formatted(start, end));
    }
  }
}
