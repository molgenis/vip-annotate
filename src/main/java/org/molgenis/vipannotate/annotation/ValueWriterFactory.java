package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.FloatType;
import org.molgenis.vipannotate.annotation.resolved.IntType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Numbers;

@RequiredArgsConstructor
public final class ValueWriterFactory {
  public IntValueWriter createIntValueWriter(IntType intType) {
    return switch (intType) {
      case I8 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeLongToByte(value)),
              Byte.BYTES);
      case U8 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeLongToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeLongToShort(value)),
              Short.BYTES);
      case U16 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeLongToUnsignedShort(value)),
              Short.BYTES);
      case I32 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putIntUnchecked(Numbers.safeLongToInt(value)),
              Integer.BYTES);
      case U32 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putIntUnchecked(Numbers.safeLongToUnsignedInt(value)),
              Integer.BYTES);
      case I64, U64 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putLongUnchecked(value),
              Long.BYTES);
    };
  }

  public IntValueWriter createIndexedIntValueWriter(IntType intType) {
    return switch (intType) {
      case I8 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, Numbers.safeLongToByte(value)),
              Byte.BYTES);
      case U8 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(
                      index, Numbers.safeLongToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(index, Numbers.safeLongToShort(value)),
              Short.BYTES);
      case U16 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(
                      index, Numbers.safeLongToUnsignedShort(value)),
              Short.BYTES);
      case I32 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(index, Numbers.safeLongToInt(value)),
              Integer.BYTES);
      case U32 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(index, Numbers.safeLongToUnsignedInt(value)),
              Integer.BYTES);
      case I64, U64 ->
          new IntValueWriter(
              (long value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setLongAtIndexUnchecked(index, value),
              Long.BYTES);
    };
  }

  public FloatValueWriter createFloatValueWriter(FloatType floatType) {
    // TODO write actual float and double
    return switch (floatType) {
      case F32 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putIntUnchecked(Float.floatToRawIntBits((float) value)),
              Float.BYTES);
      case F64 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putLongUnchecked(Double.doubleToRawLongBits(value)),
              Double.BYTES);
    };
  }

  public FloatValueWriter createIndexedFloatValueWriter(FloatType floatType) {
    // TODO write actual float and double
    return switch (floatType) {
      case F32 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(
                      index, Float.floatToRawIntBits((float) value)),
              Float.BYTES);
      case F64 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setLongAtIndexUnchecked(index, Double.doubleToRawLongBits(value)),
              Double.BYTES);
    };
  }
}
