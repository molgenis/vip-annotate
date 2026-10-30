package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.def.*;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.bed.BedFeature;
import org.molgenis.vipannotate.format.bed.BedField;
import org.molgenis.vipannotate.format.bed.Chrom;
import tools.jackson.core.internal.shaded.fdp.JavaDoubleParser;

@RequiredArgsConstructor
public final class BedAnnotatedPositionMapper
    implements Function<BedFeature, AnnotatedPosition<CompositeAnnotation>> {
  private final BedInputFormat bedInputFormat;
  private final AnnotationsSpec annotationsSpec;

  @Override
  public AnnotatedPosition<CompositeAnnotation> apply(BedFeature bedFeature) {
    Position position = createPosition(bedFeature);
    CompositeAnnotation annotation = createAnnotation(bedFeature);
    return new AnnotatedPosition<>(position, annotation);
  }

  private Position createPosition(BedFeature bedFeature) {
    Chrom chrom = bedFeature.getChrom();
    long chromStart = bedFeature.getChromStart().getRaw();
    long chromEnd = bedFeature.getChromEnd().getRaw();
    if (chromEnd - chromStart != 1) {
      throw new IllegalArgumentException("expected bed position instead of interval");
    }
    // FIXME hardcoded length
    // FIXME use contig registry
    Contig contig = new Contig(chrom.getRaw().toString(), 9);
    // FIXME use long for pos
    return new Position(contig, Math.toIntExact(chromStart + 1L)); // 0-based -> 1-based
  }

  // FIXME dedup with TsvAnnotatedSequenceVariantMapper
  private CompositeAnnotation createAnnotation(BedFeature bedFeature) {
    Map<String, BedFieldType> idxAnnotations = bedInputFormat.annotations();
    if (idxAnnotations.isEmpty()) {
      throw new IllegalArgumentException();
      //    }
      //    else if (idxAnnotations.length == 1) {
      //      int idxAnnotation = idxAnnotations[0];
      //      return (T) new
      // DoubleAnnotation(JavaDoubleParser.parseDouble(tsvFeature[idxAnnotation]));
    } else {
      List<Annotation> annotations = new ArrayList<>(annotationsSpec.size());
      annotationsSpec.forEach(
          (annotationDatasetId, annotationSpec) -> {
            BedFieldType bedFieldType = idxAnnotations.get(annotationDatasetId);
            if (bedFieldType == null) {
              throw new IllegalArgumentException(
                  "'schema.annotation_datasets.%s' not defined in 'input.annotations'"
                      .formatted(annotationDatasetId));
            }
            annotations.add(createAnnotation(bedFeature, bedFieldType, annotationSpec));
          });
      return new CompositeAnnotation(annotations.toArray(new Annotation[0]));
    }
  }

  private Annotation createAnnotation(
      BedFeature bedFeature, BedFieldType bedFieldType, AnnotationSpec annotationSpec) {
    BedField bedField =
        switch (bedFieldType) {
          case CHROM -> bedFeature.getChrom();
          case CHROM_START -> bedFeature.getChromStart();
          case CHROM_END -> bedFeature.getChromEnd();
          case NAME -> bedFeature.getName();
          case SCORE -> bedFeature.getScore();
          case STRAND, THICK_START, THICK_END, ITEM_RGB, BLOCK_COUNT, BLOCK_SIZES, BLOCK_STARTS ->
              throw new UnsupportedOperationException(); // FIXME
        };

    return switch (annotationSpec) {
      case EnumAnnotationSpec _ -> createEnumAnnotation(bedField);
      case EnumSetAnnotationSpec _ -> createEnumSetAnnotation(bedField);
      case FloatAnnotationSpec _ -> createFloatAnnotation(bedField);
      case IntAnnotationSpec _ -> createIntAnnotation(bedField);
    };
  }

  private Annotation createEnumAnnotation(BedField bedField) {
    return new StringAnnotation(
        !bedField.getRawView().isEmpty() ? bedField.getRawView().toString() : null);
  }

  private Annotation createEnumSetAnnotation(BedField bedField) {
    StringListAnnotation annotation = new StringListAnnotation();
    List<String> values = annotation.getValues();
    bedField.parseValuesAppendTo(values);
    values.forEach(annotation::add);
    return annotation;
  }

  private Annotation createFloatAnnotation(BedField bedField) {
    return !bedField.getRawView().isEmpty()
        ? new FloatAnnotation(JavaDoubleParser.parseDouble(bedField.getRawView()))
        : new FloatAnnotation();
  }

  private Annotation createIntAnnotation(BedField bedField) {
    return !bedField.getRawView().isEmpty()
        ? new IntAnnotation(
            Integer.parseInt(bedField.getRawView(), 0, bedField.getRawView().length(), 10))
        : new IntAnnotation();
  }
}
