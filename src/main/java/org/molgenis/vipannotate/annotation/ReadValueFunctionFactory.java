package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.FloatType;
import org.molgenis.vipannotate.annotation.spec.IntType;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public final class ReadValueFunctionFactory {

  public IntReadValueFunction createIntReadValueFunction(IntType intType) {
    return switch (intType) {
      case I8 -> BinaryReader::getByteAtIndex;
      case U8 -> BinaryReader::getUnsignedByteAtIndex;
      case I16 -> BinaryReader::getShortAtIndex;
      case U16 -> BinaryReader::getUnsignedShortAtIndex;
      case I32 -> BinaryReader::getIntAtIndex;
      case U32 -> BinaryReader::getUnsignedIntAtIndex;
      // caller is responsible for converting unsigned long using e.g. Long.toUnsignedString(value)
      case I64, U64 -> BinaryReader::getLongAtIndex;
    };
  }

  public FloatReadValueFunction createFloatReadValueFunction(FloatType floatType) {
    return switch (floatType) {
      case F32 -> BinaryReader::getFloatAtIndex;
      case F64 -> BinaryReader::getDoubleAtIndex;
    };
  }

  public static ReadValueFunctionFactory create() {
    return new ReadValueFunctionFactory();
  }
}
