package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.FloatType;
import org.molgenis.vipannotate.annotation.spec.IntType;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.Numbers;

@RequiredArgsConstructor
public final class ValueWriterFactory {
  public IntValueWriter createIntValueWriter(IntType intType) {
    return switch (intType) {
      case I8 ->
          new IntValueWriter(
              (long value, MemoryEncoder memEncoder) ->
                  memEncoder.putByte(Numbers.safeLongToByte(value)),
              Byte.BYTES);
      case U8 ->
          new IntValueWriter(
              (long value, MemoryEncoder memEncoder) ->
                  memEncoder.putByte(Numbers.safeLongToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (long value, MemoryEncoder memEncoder) ->
                  memEncoder.putShort(Numbers.safeLongToShort(value)),
              Short.BYTES);
      case U16 ->
          new IntValueWriter(
              (long value, MemoryEncoder memEncoder) ->
                  memEncoder.putShort(Numbers.safeLongToUnsignedShort(value)),
              Short.BYTES);
      case I32 ->
          new IntValueWriter(
              (long value, MemoryEncoder memEncoder) ->
                  memEncoder.putInt(Numbers.safeLongToInt(value)),
              Integer.BYTES);
      case U32 ->
          new IntValueWriter(
              (long value, MemoryEncoder memEncoder) ->
                  memEncoder.putInt(Numbers.safeLongToUnsignedInt(value)),
              Integer.BYTES);
      case I64, U64 ->
          new IntValueWriter(
              (long value, MemoryEncoder memEncoder) -> memEncoder.putLong(value), Long.BYTES);
    };
  }

  public FloatValueWriter createFloatValueWriter(FloatType floatType) {
    return switch (floatType) {
      case F32 ->
          new FloatValueWriter(
              (double value, MemoryEncoder memEncoder) -> memEncoder.putFloat((float) value),
              Float.BYTES);
      case F64 ->
          new FloatValueWriter(
              (double value, MemoryEncoder memEncoder) -> memEncoder.putDouble(value),
              Double.BYTES);
    };
  }
}
