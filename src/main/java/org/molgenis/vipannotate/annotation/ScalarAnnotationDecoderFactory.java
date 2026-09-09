package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IntInterval;
import org.molgenis.vipannotate.util.Quantizer;

public class ScalarAnnotationDecoderFactory {
  public AnnotationDecoder<ScalarAnnotation> create(AnnotationValue annotationValue) {
    if (annotationValue.encoding() == null) {
      // FIXME handle other logical types
      ScalarLogicalType logicalType = (ScalarLogicalType) annotationValue.logicalType();
      StorageType storageType = annotationValue.storageType();
      ReadValueFunction readValueFunction = createReadValueFunction(storageType);

      if (logicalType.nullable()) {
        if (logicalType.range() != null) {
          return switch (logicalType.range()) {
            case Range.FloatingPointRange floatingPointRange ->
                throw new UnsupportedOperationException();
            // FIXME AnnotationDecoder cast
            // FIXME int cast
            case Range.IntegerRange integerRange ->
                (AnnotationDecoder<ScalarAnnotation>)
                    (AnnotationDecoder<?>)
                        new OffsetNullableIntAnnotationDecoder(
                            readValueFunction, (int) integerRange.min());
          };
        } else {
          return switch (storageType.scalarType()) {
            case I8, I16, I32, U8, U16 ->
                (AnnotationDecoder<ScalarAnnotation>)
                    (AnnotationDecoder<?>) new NullableIntAnnotationDecoder(readValueFunction);
            case I64, U32, U64, F32, F64 -> {
              // FIXME support null encoding for U64,F32,F64
              throw new UnsupportedOperationException();
            }
          };
        }
      } else {
        if (logicalType.range() != null) {
          // FIXME implement
          throw new UnsupportedOperationException();
        } else {
          return switch (storageType.scalarType()) {
            case I8, I16, I32, U8, U16 ->
                (AnnotationDecoder<ScalarAnnotation>)
                    (AnnotationDecoder<?>) new IntAnnotationDecoder(readValueFunction);
            case I64, U32, U64, F32, F64 -> {
              // FIXME support null encoding for U64,F32,F64
              throw new UnsupportedOperationException();
            }
          };
        }
      }
    }

    return switch (annotationValue.encoding()) {
      case EnumEncoding enumEncoding -> {
        // FIXME implement
        throw new UnsupportedOperationException();
      }
      case QuantizedEncoding quantizedEncoding ->
          createQuantizedAnnotationDecoder(
              annotationValue.storageType(),
              // FIXME remove cast
              (ScalarLogicalType) annotationValue.logicalType(),
              quantizedEncoding);
    };
  }

  private static QuantizedAnnotationDecoder createQuantizedAnnotationDecoder(
      StorageType storageType, ScalarLogicalType logicalType, QuantizedEncoding encoding) {
    Quantizer quantizer = createQuantizer(logicalType, encoding);

    ReadValueFunction readValueFunction = createReadValueFunction(storageType);
    return new QuantizedAnnotationDecoder(quantizer, readValueFunction, encoding.nullCode());
  }

  private static Quantizer createQuantizer(
      ScalarLogicalType logicalType, QuantizedEncoding encoding) {
    if (logicalType.scalarType() != ScalarType.F64) {
      throw new IllegalArgumentException();
    }
    if (logicalType.nullable() && encoding.nullCode() == null) {
      throw new IllegalArgumentException();
    }
    if (!logicalType.nullable() && encoding.nullCode() != null) {
      throw new IllegalArgumentException();
    }
    QuantizedEncoding.Range range = encoding.range();
    QuantizedEncoding.Levels levels = encoding.levels();
    return new Quantizer(
        new DoubleInterval(range.min(), range.max()), new IntInterval(levels.min(), levels.max()));
  }

  private static ReadValueFunction createReadValueFunction(StorageType storageType) {
    return switch (storageType.scalarType()) {
      case I8 -> MemoryBuffer::getByteAtIndex;
      case I16 -> MemoryBuffer::getShortAtIndex;
      case I32 -> MemoryBuffer::getIntAtIndex;
      case U8 -> MemoryBuffer::getUnsignedByteAtIndex;
      case U16 -> MemoryBuffer::getUnsignedShortAtIndex;
      default -> throw new IllegalArgumentException();
    };
  }
}
