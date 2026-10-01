package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.AnnotatedFeatureReaderFactory.TsvColumnsSpec;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.tsv.TsvField;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import tools.jackson.core.internal.shaded.fdp.JavaDoubleParser;

@RequiredArgsConstructor
public final class TsvAnnotationMapper {
  private final TsvColumnsSpec tsvColumnsSpec;
  private final AnnotationsSpec annotationSpecs;

  public CompositeAnnotation createAnnotation(TsvRecord tsvRecord) {
    if (annotationSpecs.isEmpty()) {
      throw new IllegalArgumentException();
    }

    List<Annotation> annotations = new ArrayList<>(annotationSpecs.size());

    annotationSpecs.forEach(
        (annotationDatasetId, annotationSpec) -> {
          Integer annotationIndex = tsvColumnsSpec.annotations().get(annotationDatasetId);

          if (annotationIndex == null) {
            throw new IllegalArgumentException(
                "'definition.annotations.%s' not defined in 'input.colums.annotations'"
                    .formatted(annotationDatasetId));
          }

          annotations.add(createAnnotation(tsvRecord.field(annotationIndex), annotationSpec));
        });

    return new CompositeAnnotation(annotations.toArray(new Annotation[0]));
  }

  private Annotation createAnnotation(TsvField tsvField, AnnotationSpec annotationSpec) {
    return switch (annotationSpec) {
      case EnumAnnotationSpec _ -> createEnumAnnotation(tsvField);
      case EnumSetAnnotationSpec _ -> createEnumSetAnnotation(tsvField);
      case FloatAnnotationSpec _ -> createFloatAnnotation(tsvField);
      case IntAnnotationSpec _ -> createIntAnnotation(tsvField);
    };
  }

  private Annotation createEnumAnnotation(TsvField tsvField) {
    return new StringAnnotation(
        !tsvField.isMissingValue() ? tsvField.getRawView().toString() : null);
  }

  private Annotation createEnumSetAnnotation(TsvField tsvField) {
    return new StringListAnnotation(tsvField.parseValues());
  }

  private Annotation createFloatAnnotation(TsvField tsvField) {
    return !tsvField.isMissingValue()
        ? new FloatAnnotation(JavaDoubleParser.parseDouble(tsvField.getRawView()))
        : new FloatAnnotation();
  }

  private Annotation createIntAnnotation(TsvField tsvField) {
    return !tsvField.isMissingValue()
        ? new IntAnnotation(
            Integer.parseInt(tsvField.getRawView(), 0, tsvField.getRawView().length(), 10))
        : new IntAnnotation();
  }
}
