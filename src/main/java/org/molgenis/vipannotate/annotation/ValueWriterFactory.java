package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.FloatType;
import org.molgenis.vipannotate.annotation.spec.IntType;
import org.molgenis.vipannotate.serialization.BinaryWriter;
import org.molgenis.vipannotate.util.Numbers;

@RequiredArgsConstructor
public final class ValueWriterFactory {
  public IntValueWriter createIntValueWriter(IntType intType) {
    return switch (intType) {
      case I8 ->
          new IntValueWriter(
              (long value, BinaryWriter binaryWriter) ->
                  binaryWriter.writeByte(Numbers.safeLongToByte(value)),
              Byte.BYTES);
      case U8 ->
          new IntValueWriter(
              (long value, BinaryWriter binaryWriter) ->
                  binaryWriter.writeByte(Numbers.safeLongToUnsignedByte(value)),
              Byte.BYTES);
      case I16 ->
          new IntValueWriter(
              (long value, BinaryWriter binaryWriter) ->
                  binaryWriter.writeShort(Numbers.safeLongToShort(value)),
              Short.BYTES);
      case U16 ->
          new IntValueWriter(
              (long value, BinaryWriter binaryWriter) ->
                  binaryWriter.writeShort(Numbers.safeLongToUnsignedShort(value)),
              Short.BYTES);
      case I32 ->
          new IntValueWriter(
              (long value, BinaryWriter binaryWriter) ->
                  binaryWriter.writeInt(Numbers.safeLongToInt(value)),
              Integer.BYTES);
      case U32 ->
          new IntValueWriter(
              (long value, BinaryWriter binaryWriter) ->
                  binaryWriter.writeInt(Numbers.safeLongToUnsignedInt(value)),
              Integer.BYTES);
      case I64, U64 ->
          new IntValueWriter(
              (long value, BinaryWriter binaryWriter) -> binaryWriter.writeLong(value), Long.BYTES);
    };
  }

  public FloatValueWriter createFloatValueWriter(FloatType floatType) {
    return switch (floatType) {
      case F32 ->
          new FloatValueWriter(
              (double value, BinaryWriter binaryWriter) -> binaryWriter.writeFloat((float) value),
              Float.BYTES);
      case F64 ->
          new FloatValueWriter(
              (double value, BinaryWriter binaryWriter) -> binaryWriter.writeDouble(value),
              Double.BYTES);
    };
  }
}
