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
    return switch (annotationSpec.storageType()) {
      case FloatType type -> create(type, annotationSpec.floatEncoding());
      case IntType type -> create(type, annotationSpec.floatEncoding());
    };
  }

  private AnnotationEncoder<?> create(FloatType floatType, FloatEncoding floatEncoding) {
    FloatValueWriter valueWriter = valueWriterFactory.createFloatValueWriter(floatType);

    return switch (floatEncoding) {
      case NullableFloatEncoding _ -> new NullableFloatAnnotationEncoder(valueWriter);
      case PlainFloatEncoding _ -> new FloatAnnotationEncoder(valueWriter);
      default -> throw new UnsupportedOperationException();
    };
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private AnnotationEncoder<?> create(IntType intType, FloatEncoding floatEncoding) {
    IntValueWriter valueWriter = valueWriterFactory.createIntValueWriter(intType);

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
