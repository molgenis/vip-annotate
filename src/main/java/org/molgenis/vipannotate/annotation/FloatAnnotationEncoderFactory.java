package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.annotation.resolved.ResolvedFloatAnnotationSpec;
import org.molgenis.vipannotate.util.DoubleInterval;
import org.molgenis.vipannotate.util.IntInterval;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class FloatAnnotationEncoderFactory {
  private final ValueWriterFactory valueWriterFactory;

  public AnnotationEncoder<?> create(ResolvedFloatAnnotationSpec annotationSpec) {
    return create(annotationSpec, false);
  }

  public AnnotationEncoder<?> createIndexed(ResolvedFloatAnnotationSpec annotationSpec) {
    return create(annotationSpec, true);
  }

  private AnnotationEncoder<?> create(ResolvedFloatAnnotationSpec annotationSpec, boolean indexed) {
    return switch (annotationSpec.storageType()) {
      case FloatType type -> create(type, annotationSpec.floatEncoding(), indexed);
      case IntType type -> create(type, annotationSpec.floatEncoding(), indexed);
    };
  }

  private AnnotationEncoder<?> create(
      FloatType floatType, FloatEncoding floatEncoding, boolean indexed) {
    FloatValueWriter valueWriter =
        indexed
            ? valueWriterFactory.createIndexedFloatValueWriter(floatType)
            : valueWriterFactory.createFloatValueWriter(floatType);

    return switch (floatEncoding) {
      case NullableFloatEncoding _ -> new NullableFloatAnnotationEncoder(valueWriter);
      case PlainFloatEncoding _ -> new FloatAnnotationEncoder(valueWriter);
      default -> throw new UnsupportedOperationException();
    };
  }

  private AnnotationEncoder<?> create(
      IntType intType, FloatEncoding floatEncoding, boolean indexed) {
    IntValueWriter valueWriter =
        indexed
            ? valueWriterFactory.createIndexedIntValueWriter(intType)
            : valueWriterFactory.createIntValueWriter(intType);

    return switch (floatEncoding) {
      case QuantizedEncoding encoding ->
          new QuantizedAnnotationEncoder(
              new Quantizer(
                  new DoubleInterval(encoding.range().min(), encoding.range().max()),
                  new IntInterval(encoding.levels().min(), encoding.levels().max())),
              valueWriter,
              encoding.nullCode());
      default -> throw new UnsupportedOperationException();
    };
  }
}
