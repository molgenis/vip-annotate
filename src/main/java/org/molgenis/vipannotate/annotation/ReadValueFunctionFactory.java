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
      case I16 -> MemoryBuffer::getShortAtIndex;
      case I32 -> MemoryBuffer::getIntAtIndex;
      case I64 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
      case U8 -> MemoryBuffer::getUnsignedByteAtIndex;
      case U16 -> MemoryBuffer::getUnsignedShortAtIndex;
      case U32 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
      case U64 -> throw new UnsupportedOperationException("Not implemented yet"); // FIXME implement
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
