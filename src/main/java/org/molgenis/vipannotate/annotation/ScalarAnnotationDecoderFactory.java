package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.annotation.spec.AnnotationDataset;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IntInterval;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class ScalarAnnotationDecoderFactory {
  private final ReadValueFunctionFactory readValueFunctionFactory;

  public AnnotationDecoder<ScalarAnnotation> create(AnnotationDataset annotationDataset) {
    if (annotationDataset.encoding() == null) {
      // FIXME handle other logical types
      ScalarLogicalType logicalType = (ScalarLogicalType) annotationDataset.logicalType();
      ScalarType storageScalarType = logicalType.scalarType();

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
                            readValueFunctionFactory.createIntReadValueFunction(storageScalarType),
                            (int) integerRange.min());
          };
        } else {
          return switch (storageScalarType) {
            case I8, I16, I32, U8, U16 ->
                (AnnotationDecoder<ScalarAnnotation>)
                    (AnnotationDecoder<?>)
                        new NullableIntAnnotationDecoder(
                            readValueFunctionFactory.createIntReadValueFunction(storageScalarType));
            case F32, F64 ->
                (AnnotationDecoder<ScalarAnnotation>)
                    (AnnotationDecoder<?>)
                        new NullableFloatAnnotationDecoder(
                            readValueFunctionFactory.createFloatReadValueFunction(
                                storageScalarType));
            case I64, U32, U64 -> // FIXME support null encoding for I64, U32, U64
                throw new UnsupportedOperationException();
          };
        }
      } else {
        if (logicalType.range() != null) {
          // FIXME implement
          throw new UnsupportedOperationException();
        } else {
          return switch (storageScalarType) {
            case I8, I16, I32, U8, U16 ->
                (AnnotationDecoder<ScalarAnnotation>)
                    (AnnotationDecoder<?>)
                        new IntAnnotationDecoder(
                            readValueFunctionFactory.createIntReadValueFunction(storageScalarType));
            case F32, F64 ->
                (AnnotationDecoder<ScalarAnnotation>)
                    (AnnotationDecoder<?>)
                        new FloatAnnotationDecoder(
                            readValueFunctionFactory.createFloatReadValueFunction(
                                storageScalarType));
            case I64, U32, U64 -> // FIXME support null encoding for I64, U32, U64
                throw new UnsupportedOperationException();
          };
        }
      }
    }

    return switch (annotationDataset.encoding()) {
      case EnumEncoding enumEncoding -> {
        // FIXME implement
        throw new UnsupportedOperationException();
      }
      case QuantizedEncoding quantizedEncoding ->
          createQuantizedAnnotationDecoder(
              annotationDataset.storageType(),
              // FIXME remove cast
              (ScalarLogicalType) annotationDataset.logicalType(),
              quantizedEncoding);
    };
  }

  private QuantizedAnnotationDecoder createQuantizedAnnotationDecoder(
      StorageType storageType, ScalarLogicalType logicalType, QuantizedEncoding encoding) {
    Quantizer quantizer = createQuantizer(logicalType, encoding);

    IntReadValueFunction intReadValueFunction =
        readValueFunctionFactory.createIntReadValueFunction(storageType.scalarType());
    return new QuantizedAnnotationDecoder(quantizer, intReadValueFunction, encoding.nullCode());
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
}
