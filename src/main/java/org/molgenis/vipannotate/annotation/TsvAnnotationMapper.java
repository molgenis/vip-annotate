package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.AnnotatedFeatureReaderFactory.ResolvedTsvColumns;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.tsv.TsvField;
import org.molgenis.vipannotate.format.tsv.TsvRecord;

@RequiredArgsConstructor
public final class TsvAnnotationMapper {
  private final ResolvedTsvColumns resolvedTsvColumns;
  private final ResolvedAnnotationSpecs annotationSpecs;

  public CompositeAnnotation createAnnotation(TsvRecord tsvRecord) {
    if (annotationSpecs.isEmpty()) {
      throw new IllegalArgumentException();
    }

    List<Annotation> annotations = new ArrayList<>(annotationSpecs.size());

    annotationSpecs.forEach(
        (annotationDatasetId, annotationSpec) -> {
          Integer annotationIndex = resolvedTsvColumns.annotations().get(annotationDatasetId);

          if (annotationIndex == null) {
            throw new IllegalArgumentException(
                "'definition.annotations.%s' not defined in 'input.colums.annotations'"
                    .formatted(annotationDatasetId));
          }

          annotations.add(createAnnotation(tsvRecord.field(annotationIndex), annotationSpec));
        });

    return new CompositeAnnotation(annotations.toArray(new Annotation[0]));
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedAnnotationSpec annotationSpec) {
    return switch (annotationSpec) {
      case ResolvedEnumAnnotationSpec spec -> createAnnotation(tsvField, spec);
      case ResolvedEnumSetAnnotationSpec spec -> createAnnotation(tsvField, spec);
      case ResolvedFloatAnnotationSpec spec -> createAnnotation(tsvField, spec);
      case ResolvedIntAnnotationSpec spec -> createAnnotation(tsvField, spec);
    };
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedEnumAnnotationSpec spec) {
    return new StringAnnotation(
        !tsvField.isMissingValue() ? tsvField.getRawView().toString() : null);
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedEnumSetAnnotationSpec spec) {
    return new StringListAnnotation(tsvField.parseValues());
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedFloatAnnotationSpec spec) {
    return switch (spec.floatEncoding()) {
      case NullableFloatEncoding _ ->
          !tsvField.isMissingValue()
              ? new NullableFloatAnnotation(Double.parseDouble(tsvField.getRawView().toString()))
              : new NullableFloatAnnotation();

      case PlainFloatEncoding _ ->
          new FloatAnnotation(Double.parseDouble(tsvField.getRawView().toString()));

      case QuantizedEncoding encoding ->
          encoding.nullCode() != null
              ? (!tsvField.isMissingValue()
                  ? new NullableFloatAnnotation(
                      Double.parseDouble(tsvField.getRawView().toString()))
                  : new NullableFloatAnnotation())
              : new FloatAnnotation(Double.parseDouble(tsvField.getRawView().toString()));
    };
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedIntAnnotationSpec spec) {
    if (spec.intEncoding() instanceof NullableIntEncoding
        || spec.intEncoding() instanceof OffsetNullableIntEncoding) {
      return !tsvField.isMissingValue()
          ? new NullableIntAnnotation(
              Integer.parseInt(tsvField.getRawView(), 0, tsvField.getRawView().length(), 10))
          : new NullableIntAnnotation();
    }

    return new IntAnnotation(
        Integer.parseInt(tsvField.getRawView(), 0, tsvField.getRawView().length(), 10));
  }
}
