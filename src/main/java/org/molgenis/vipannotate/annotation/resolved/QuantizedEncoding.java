package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;

public record QuantizedEncoding(Range range, Levels levels, @Nullable Integer nullCode)
    implements FloatEncoding {

  public record Range(double min, double max) {
    public Range {
      if (min > max) {
        throw new IllegalArgumentException("min > max");
      }
    }
  }

  public record Levels(int min, int max) {
    public Levels {
      if (min > max) {
        throw new IllegalArgumentException("min > max");
      }
    }
  }
}
