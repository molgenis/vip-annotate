package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.bed.BedFeature;
import org.molgenis.vipannotate.format.bed.BedField;
import org.molgenis.vipannotate.format.bed.Chrom;

@RequiredArgsConstructor
public final class BedAnnotatedPositionMapper
    implements Function<BedFeature, AnnotatedPosition<CompositeAnnotation>> {
  private final BedInputFormat bedInputFormat;
  private final ResolvedAnnotationSpecs annotationsSpecs;

  @Override
  public AnnotatedPosition<CompositeAnnotation> apply(BedFeature bedFeature) {
    Position position = createPosition(bedFeature);
    CompositeAnnotation annotation = createAnnotation(bedFeature);
    return new AnnotatedPosition<>(position, annotation);
  }

  private Position createPosition(BedFeature bedFeature) {
    Chrom chrom = bedFeature.getChrom();
    int chromStart = bedFeature.getChromStart().getRaw();
    int chromEnd = bedFeature.getChromEnd().getRaw();
    if (chromEnd - chromStart != 1) {
      throw new IllegalArgumentException("expected bed position instead of interval");
    }
    // FIXME hardcoded length
    // FIXME use contig registry
    Contig contig = new Contig(chrom.getRaw().toString(), 9);
    return new Position(contig, chromStart + 1); // 0-based -> 1-based
  }

  // FIXME dedup with TsvAnnotatedSequenceVariantMapper
  private CompositeAnnotation createAnnotation(BedFeature bedFeature) {
    Map<String, BedFieldType> idxAnnotations = bedInputFormat.annotations();
    if (idxAnnotations.isEmpty()) {
      throw new IllegalArgumentException();
      //    }
      //    else if (idxAnnotations.length == 1) {
      //      int idxAnnotation = idxAnnotations[0];
      //      return (T) new DoubleAnnotation(Double.parseDouble(tsvFeature[idxAnnotation]));
    } else {
      List<Annotation> annotations = new ArrayList<>(annotationsSpecs.size());
      annotationsSpecs.forEach(
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
      BedFeature bedFeature, BedFieldType bedFieldType, ResolvedAnnotationSpec annotationSpec) {
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
      case ResolvedEnumAnnotationSpec spec -> createAnnotation(bedField, spec);
      case ResolvedEnumSetAnnotationSpec spec -> createAnnotation(bedField, spec);
      case ResolvedFloatAnnotationSpec spec -> createAnnotation(bedField, spec);
      case ResolvedIntAnnotationSpec spec -> createAnnotation(bedField, spec);
    };
  }

  private Annotation createAnnotation(BedField bedField, ResolvedEnumAnnotationSpec spec) {
    return new StringAnnotation(
        !bedField.getRawView().isEmpty() ? bedField.getRawView().toString() : null);
  }

  private Annotation createAnnotation(BedField bedField, ResolvedEnumSetAnnotationSpec spec) {
    // TODO perf: split that does not require toString
    String[] tokens =
        !bedField.getRawView().isEmpty()
            ? bedField.getRawView().toString().split(",", -1)
            : new String[0];
    return new StringListAnnotation(tokens);
  }

  private Annotation createAnnotation(BedField bedField, ResolvedFloatAnnotationSpec spec) {
    return switch (spec.floatEncoding()) {
      case NullableFloatEncoding _ ->
          !bedField.getRawView().isEmpty()
              ? new NullableFloatAnnotation(Double.parseDouble(bedField.getRawView().toString()))
              : new NullableFloatAnnotation();
      case PlainFloatEncoding _ ->
          new FloatAnnotation(Double.parseDouble(bedField.getRawView().toString()));
      case QuantizedEncoding encoding ->
          encoding.nullCode() != null
              ? (!bedField.getRawView().isEmpty()
                  ? new NullableFloatAnnotation(
                      Double.parseDouble(bedField.getRawView().toString()))
                  : new NullableFloatAnnotation())
              : new FloatAnnotation(Double.parseDouble(bedField.getRawView().toString()));
    };
  }

  private Annotation createAnnotation(BedField bedField, ResolvedIntAnnotationSpec spec) {
    // FIXME don't check with instanceof
    if (spec.intEncoding() instanceof NullableIntEncoding
        || spec.intEncoding() instanceof OffsetNullableIntEncoding) {
      // TODO perf: reuse new NullableIntAnnotation()
      return !bedField.getRawView().isEmpty()
          ? new NullableIntAnnotation(
              Integer.parseInt(bedField.getRawView(), 0, bedField.getRawView().length(), 10))
          : new NullableIntAnnotation();
    } else {
      return new IntAnnotation(
          Integer.parseInt(bedField.getRawView(), 0, bedField.getRawView().length(), 10));
    }
  }
}
