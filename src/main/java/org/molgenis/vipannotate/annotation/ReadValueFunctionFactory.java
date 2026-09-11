package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.StorageType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public final class ReadValueFunctionFactory {
  public IntReadValueFunction createIntReadValueFunction(StorageType storageType) {
    return switch (storageType.scalarType()) {
      case I8 -> MemoryBuffer::getByteAtIndex;
      case I16 -> MemoryBuffer::getShortAtIndex;
      case I32 -> MemoryBuffer::getIntAtIndex;
      case U8 -> MemoryBuffer::getUnsignedByteAtIndex;
      case U16 -> MemoryBuffer::getUnsignedShortAtIndex;
      default -> throw new IllegalArgumentException();
    };
  }

  public static FloatReadValueFunction createFloatReadValueFunction(StorageType storageType) {
    return switch (storageType.scalarType()) {
      // FIXME introduce memoryBuffer float read operations
      case F32 -> (memoryBuffer, index) -> Float.intBitsToFloat(memoryBuffer.getIntAtIndex(index));
      // FIXME introduce memoryBuffer double read operations
      case F64 ->
          (memoryBuffer, index) -> Double.longBitsToDouble(memoryBuffer.getLongAtIndex(index));
      default -> throw new IllegalArgumentException();
    };
  }
}
