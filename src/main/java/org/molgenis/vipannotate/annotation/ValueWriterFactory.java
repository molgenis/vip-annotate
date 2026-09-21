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
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeIntToByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeIntToShort(value)),
              Short.BYTES);
      case I32 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) -> memoryBuffer.putIntUnchecked(value),
              Integer.BYTES);
      case I64 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
      case U8 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeIntToUnsignedByte(value)),
              Byte.BYTES);
      case U16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeIntToUnsignedShort(value)),
              Short.BYTES);
      case U32 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) -> memoryBuffer.putIntUnchecked(value),
              Integer.BYTES);
      case U64 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
    };
  }

  public IntValueWriter createIndexedIntValueWriter(IntType intType) {
    return switch (intType) {
      case I8 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, Numbers.safeIntToByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(index, Numbers.safeIntToShort(value)),
              Short.BYTES);
      case I32 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(index, value),
              Integer.BYTES);
      case I64 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
      case U8 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, Numbers.safeIntToUnsignedByte(value)),
              Byte.BYTES);

      case U16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(
                      index, Numbers.safeIntToUnsignedShort(value)),
              Short.BYTES);
      case U32 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
      case U64 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
    };
  }

  public FloatValueWriter createFloatValueWriter(FloatType floatType) {
    return switch (floatType) {
      case F32 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(
                      index, Float.floatToRawIntBits((float) value)),
              Float.BYTES);
      // FIXME introduce memoryBuffer double write operations
      case F64 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int index) -> {
                throw new UnsupportedOperationException();
              },
              Double.BYTES);
    };
  }

  public FloatValueWriter createIndexedFloatValueWriter(FloatType floatType) {
    return switch (floatType) {
      case F32 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(
                      index, Float.floatToRawIntBits((float) value)),
              Float.BYTES);
      // FIXME introduce memoryBuffer double write operations
      case F64 ->
          new FloatValueWriter(
              (double value, MemoryBuffer memoryBuffer, int index) -> {
                throw new UnsupportedOperationException();
              },
              Double.BYTES);
    };
  }
}
