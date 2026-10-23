package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record QuantizedEncoding(Range range, Levels levels) implements FloatEncoding {

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

  @Override
  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putDouble(range().min());
    memEncoder.putDouble(range().max());
    memEncoder.putInt(levels().min());
    memEncoder.putInt(levels().max());
  }

  public static QuantizedEncoding readFrom(MemoryDecoder memDecoder) {
    Range range = new Range(memDecoder.getDouble(), memDecoder.getDouble());
    Levels levels = new Levels(memDecoder.getInt(), memDecoder.getInt());
    return new QuantizedEncoding(range, levels);
  }
}
