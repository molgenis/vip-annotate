package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.ScalarType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Numbers;

public final class ValueWriterFactory {
  private ValueWriterFactory() {}

  public static ValueWriter createValueWriter(ScalarType scalarType, boolean writeAtIndex) {
    if (writeAtIndex) {
      return createIndexedValueWriter(scalarType);
    } else {
      return createUnindexedValueWriter(scalarType);
    }
  }

  private static ValueWriter createIndexedValueWriter(ScalarType scalarType) {
    return switch (scalarType) {
      case I8 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, Numbers.safeIntToByte(value)),
              Byte.BYTES);
      case U8 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, Numbers.safeIntToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(index, Numbers.safeIntToShort(value)),
              Short.BYTES);
      case U16 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(
                      index, Numbers.safeIntToUnsignedShort(value)),
              Short.BYTES);
      case I32 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(index, value),
              Integer.BYTES);
      default ->
          throw new UnsupportedOperationException(
              "Unsupported scalar type: %s"
                  .formatted(scalarType)); // FIXME support U32, I64, U64, F32 and F64
    };
  }

  private static ValueWriter createUnindexedValueWriter(ScalarType scalarType) {
    return switch (scalarType) {
      case I8 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeIntToByte(value)),
              Byte.BYTES);
      case U8 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeIntToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeIntToShort(value)),
              Short.BYTES);
      case U16 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeIntToUnsignedShort(value)),
              Short.BYTES);
      case I32, U32 ->
          new ValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) -> memoryBuffer.putIntUnchecked(value),
              Integer.BYTES);
      default ->
          throw new UnsupportedOperationException(
              "Unsupported scalar type: %s"
                  .formatted(scalarType)); // FIXME support I64, U64, F32 and F64
    };
  }
}
