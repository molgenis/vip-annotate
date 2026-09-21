package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.FloatType;
import org.molgenis.vipannotate.annotation.resolved.IntType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public final class ReadValueFunctionFactory {

  public IntReadValueFunction createIntReadValueFunction(IntType intType) {
    return switch (intType) {
      case I8 -> MemoryBuffer::getByteAtIndex;
      case U8 -> MemoryBuffer::getUnsignedByteAtIndex;
      case I16 -> MemoryBuffer::getShortAtIndex;
      case U16 -> MemoryBuffer::getUnsignedShortAtIndex;
      case I32 -> MemoryBuffer::getIntAtIndex;
      case U32 -> MemoryBuffer::getUnsignedIntAtIndex;
      // caller is responsible for converting unsigned long using e.g. Long.toUnsignedString(value)
      case I64, U64 -> MemoryBuffer::getLongAtIndex;
    };
  }

  public FloatReadValueFunction createFloatReadValueFunction(FloatType floatType) {
    return switch (floatType) {
      // FIXME introduce memoryBuffer float read operations
      case F32 -> (memoryBuffer, index) -> Float.intBitsToFloat(memoryBuffer.getIntAtIndex(index));
      // FIXME introduce memoryBuffer double read operations
      case F64 ->
          (memoryBuffer, index) -> Double.longBitsToDouble(memoryBuffer.getLongAtIndex(index));
    };
  }

  public static ReadValueFunctionFactory create() {
    return new ReadValueFunctionFactory();
  }
}
