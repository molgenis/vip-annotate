package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IntInterval;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public final class ScalarAnnotationEncoderFactory {
  private final ValueWriterFactory valueWriterFactory;

  // FIXME use storage and logical type
  public AnnotationEncoder<? extends ScalarAnnotation> create(
      ScalarLogicalType logicalType,
      Encoding encoding,
      StorageType storageType,
      boolean writeAtIndex) {
    if (encoding == null) {
      if (logicalType.nullable()) {
        if (logicalType.range() != null) {
          return switch (storageType.scalarType()) {
            case I8, I16, I32, U8, U16 -> {
              Range range = logicalType.range();
              yield switch (range) {
                case Range.FloatingPointRange floatingPointRange -> {
                  // FIXME implement
                  throw new UnsupportedOperationException();
                }
                // FIXME offset encoding might not be possible: max<INT_MAX but offset+max > INT_MAX
                // FIXME don't cast
                case Range.IntegerRange integerRange ->
                    new OffsetNullableIntAnnotationEncoder(
                        valueWriterFactory.createIntValueWriter(
                            storageType.scalarType(), writeAtIndex),
                        (int) integerRange.min());
              };
            }
            case I64, U32, U64, F32, F64 -> {
              // FIXME support null encoding for U64,F32,F64
              throw new UnsupportedOperationException();
            }
          };
        } else {
          ScalarType scalarType = storageType.scalarType();
          return switch (scalarType) {
            case I8, I16, I32, U8, U16 ->
                new NullableIntAnnotationEncoder(
                    valueWriterFactory.createIntValueWriter(scalarType, writeAtIndex));
            case F32, F64 ->
                new NullableFloatAnnotationEncoder(
                    valueWriterFactory.createFloatValueWriter(scalarType, writeAtIndex));
            case I64, U32, U64 -> // FIXME support null encoding for I64, U32, U64
                throw new UnsupportedOperationException();
          };
        }
      } else {
        ScalarType scalarType = storageType.scalarType();
        return switch (scalarType) {
          case I8, I16, I32, U8, U16 ->
              new IntAnnotationEncoder(
                  valueWriterFactory.createIntValueWriter(scalarType, writeAtIndex));
          case F32, F64 ->
              new FloatAnnotationEncoder(
                  valueWriterFactory.createFloatValueWriter(scalarType, writeAtIndex));
          case I64, U32, U64 -> // FIXME support null encoding for I64, U32, U64
              throw new UnsupportedOperationException();
        };
      }
    } else {
      return switch (encoding) {
        case EnumEncoding enumEncoding -> {
          // FIXME implement
          throw new UnsupportedOperationException();
        }
        case QuantizedEncoding quantizedEncoding -> {
          // create quantizer
          QuantizedEncoding.Range range = quantizedEncoding.range();
          QuantizedEncoding.Levels levels = quantizedEncoding.levels();
          Quantizer quantizer =
              new Quantizer(
                  new DoubleInterval(range.min(), range.max()),
                  new IntInterval(levels.min(), levels.max()));

          yield new QuantizedAnnotationEncoder(
              quantizer,
              valueWriterFactory.createIntValueWriter(storageType.scalarType(), writeAtIndex),
              quantizedEncoding.nullCode());
        }
      };
    }
  }
}
