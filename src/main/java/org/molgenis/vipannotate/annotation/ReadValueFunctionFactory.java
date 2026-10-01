package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.FloatType;
import org.molgenis.vipannotate.annotation.spec.IntType;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public final class ReadValueFunctionFactory {

  public IntReadValueFunction createIntReadValueFunction(IntType intType) {
    return switch (intType) {
      case I8 -> MemoryDecoder::getByteAtIndex;
      case U8 -> MemoryDecoder::getUnsignedByteAtIndex;
      case I16 -> MemoryDecoder::getShortAtIndex;
      case U16 -> MemoryDecoder::getUnsignedShortAtIndex;
      case I32 -> MemoryDecoder::getIntAtIndex;
      case U32 -> MemoryDecoder::getUnsignedIntAtIndex;
      // caller is responsible for converting unsigned long using e.g. Long.toUnsignedString(value)
      case I64, U64 -> MemoryDecoder::getLongAtIndex;
    };
  }

  public FloatReadValueFunction createFloatReadValueFunction(FloatType floatType) {
    return switch (floatType) {
      case F32 -> MemoryDecoder::getFloatAtIndex;
      case F64 -> MemoryDecoder::getDoubleAtIndex;
    };
  }

  public static ReadValueFunctionFactory create() {
    return new ReadValueFunctionFactory();
  }
}
