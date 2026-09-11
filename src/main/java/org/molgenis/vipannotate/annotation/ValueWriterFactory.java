package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.ScalarType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Numbers;

@RequiredArgsConstructor
public final class ValueWriterFactory {

  public IntValueWriter createIntValueWriter(ScalarType scalarType, boolean writeAtIndex) {
    if (scalarType.getCategory() != ScalarType.Category.INTEGER
        || scalarType.getByteSize() > Integer.BYTES) {
      throw new IllegalArgumentException();
    }

    if (writeAtIndex) {
      return createIndexedIntValueWriter(scalarType);
    } else {
      return createUnindexedIntValueWriter(scalarType);
    }
  }

  private static IntValueWriter createIndexedIntValueWriter(ScalarType scalarType) {
    return switch (scalarType) {
      case I8 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, Numbers.safeIntToByte(value)),
              Byte.BYTES);
      case U8 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setByteAtIndexUnchecked(index, Numbers.safeIntToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(index, Numbers.safeIntToShort(value)),
              Short.BYTES);
      case U16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setShortAtIndexUnchecked(
                      index, Numbers.safeIntToUnsignedShort(value)),
              Short.BYTES);
      case I32 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int index) ->
                  memoryBuffer.setIntAtIndexUnchecked(index, value),
              Integer.BYTES);
      default -> throw new IllegalArgumentException();
    };
  }

  private static IntValueWriter createUnindexedIntValueWriter(ScalarType scalarType) {
    return switch (scalarType) {
      case I8 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeIntToByte(value)),
              Byte.BYTES);
      case U8 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putByteUnchecked(Numbers.safeIntToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeIntToShort(value)),
              Short.BYTES);
      case U16 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) ->
                  memoryBuffer.putShortUnchecked(Numbers.safeIntToUnsignedShort(value)),
              Short.BYTES);
      case I32, U32 ->
          new IntValueWriter(
              (int value, MemoryBuffer memoryBuffer, int _) -> memoryBuffer.putIntUnchecked(value),
              Integer.BYTES);
      default -> throw new IllegalArgumentException();
    };
  }

  public FloatValueWriter createFloatValueWriter(ScalarType scalarType, boolean writeAtIndex) {
    if (scalarType.getCategory() != ScalarType.Category.FLOATING_POINT) {
      throw new IllegalArgumentException();
    }

    if (writeAtIndex) {
      return createIndexedFloatValueWriter(scalarType);
    } else {
      return createUnindexedFloatValueWriter(scalarType);
    }
  }

  private static FloatValueWriter createIndexedFloatValueWriter(ScalarType scalarType) {
    return switch (scalarType) {
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
      default -> throw new IllegalArgumentException();
    };
  }

  private static FloatValueWriter createUnindexedFloatValueWriter(ScalarType scalarType) {
    return switch (scalarType) {
      case F32 ->
          new FloatValueWriter(
              (double value,
                  MemoryBuffer memoryBuffer,
                  int _) -> // TODO introduce and use memoryBuffer float write operations
              memoryBuffer.putIntUnchecked(Float.floatToRawIntBits((float) value)),
              Float.BYTES);

      case F64 ->
          new FloatValueWriter(
              (double value,
                  MemoryBuffer memoryBuffer,
                  int _) -> // TODO introduce and use memoryBuffer double write operations
              memoryBuffer.putLongUnchecked(Double.doubleToRawLongBits(value)),
              Double.BYTES);
      default -> throw new IllegalArgumentException();
    };
  }
}
