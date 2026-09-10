package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

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

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeDouble(range().min());
    binaryWriter.writeDouble(range().max());
    binaryWriter.writeInt(levels().min());
    binaryWriter.writeInt(levels().max());
    binaryWriter.writeIntNullable(nullCode);
  }

  public static QuantizedEncoding readFrom(BinaryReader binaryReader) {
    Range range = new Range(binaryReader.readDouble(), binaryReader.readDouble());
    Levels levels = new Levels(binaryReader.readInteger(), binaryReader.readInteger());
    Integer nullCode = binaryReader.readIntegerNullable();
    return new QuantizedEncoding(range, levels, nullCode);
  }
}
