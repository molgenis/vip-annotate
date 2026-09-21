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
import org.molgenis.vipannotate.format.tsv.TsvField;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.format.vcf.AltAlleleRegistry;

@RequiredArgsConstructor
public final class TsvAnnotatedSequenceVariantMapper
    implements Function<TsvRecord, AnnotatedSequenceVariant<CompositeAnnotation>> {
  private final TsvInputFormat tsvInputFormat;
  private final ResolvedAnnotationSpecs annotationsSpecs;

  @Override
  public AnnotatedSequenceVariant<CompositeAnnotation> apply(TsvRecord tsvRecord) {
    SequenceVariant sequenceVariant = createSequenceVariant(tsvRecord);
    CompositeAnnotation annotation = createAnnotation(tsvRecord);
    return new AnnotatedSequenceVariant<>(sequenceVariant, annotation);
  }

  private SequenceVariant createSequenceVariant(TsvRecord tsvRecord) {
    TsvField contigField = tsvRecord.field(tsvInputFormat.contig());
    TsvField startField = tsvRecord.field(tsvInputFormat.start());
    TsvField refField = tsvRecord.field(tsvInputFormat.ref());
    TsvField altField = tsvRecord.field(tsvInputFormat.alt());

    // FIXME hardcoded length
    // FIXME use contig registry
    Contig contig = new Contig(contigField.toString(), 9);
    int start = Integer.parseInt(startField.getRawView(), 0, startField.getRawView().length(), 10);
    switch (tsvInputFormat.coordinateSystem()) {
      case ZERO_BASED -> start++;
      case ONE_BASED -> {}
    }
    int refLen = refField.getRawView().length();
    AltAllele alt = AltAlleleRegistry.INSTANCE.getOrCreate(altField.getRawView());
    return new SequenceVariant(
        contig,
        start,
        start + refLen - 1,
        alt,
        SequenceVariantTypeDetector.determineType(refLen, alt));
  }

  private CompositeAnnotation createAnnotation(TsvRecord tsvRecord) {
    Map<String, Integer> idxAnnotations = tsvInputFormat.annotations();
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
            Integer idxAnnotation = idxAnnotations.get(annotationDatasetId);
            if (idxAnnotation == null) {
              throw new IllegalArgumentException(
                  "'schema.annotation_datasets.%s' not defined in 'input.annotations'"
                      .formatted(annotationDatasetId));
            }
            annotations.add(createAnnotation(tsvRecord.field(idxAnnotation), annotationSpec));
          });
      return new CompositeAnnotation(annotations.toArray(new Annotation[0]));
    }
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
        !tsvField.getRawView().isEmpty() ? tsvField.getRawView().toString() : null);
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedEnumSetAnnotationSpec spec) {
    // TODO perf: split that does not require toString
    String[] tokens =
        !tsvField.getRawView().isEmpty()
            ? tsvField.getRawView().toString().split(",", -1)
            : new String[0];
    return new StringListAnnotation(tokens);
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedFloatAnnotationSpec spec) {
    return switch (spec.floatEncoding()) {
      case NullableFloatEncoding encoding ->
          !tsvField.getRawView().isEmpty()
              ? new NullableFloatAnnotation(Double.parseDouble(tsvField.getRawView().toString()))
              : new NullableFloatAnnotation();
      case PlainFloatEncoding encoding ->
          new FloatAnnotation(Double.parseDouble(tsvField.getRawView().toString()));
      case QuantizedEncoding encoding ->
          encoding.nullCode() != null
              ? (!tsvField.getRawView().isEmpty()
                  ? new NullableFloatAnnotation(
                      Double.parseDouble(tsvField.getRawView().toString()))
                  : new NullableFloatAnnotation())
              : new FloatAnnotation(Double.parseDouble(tsvField.getRawView().toString()));
    };
  }

  private Annotation createAnnotation(TsvField tsvField, ResolvedIntAnnotationSpec spec) {
    // FIXME don't check with instanceof
    if (spec.intEncoding() instanceof NullableIntEncoding
        || spec.intEncoding() instanceof OffsetNullableIntEncoding) {
      // TODO perf: reuse new NullableIntAnnotation()
      return !tsvField.getRawView().isEmpty()
          ? new NullableIntAnnotation(
              Integer.parseInt(tsvField.getRawView(), 0, tsvField.getRawView().length(), 10))
          : new NullableIntAnnotation();
    } else {
      return new IntAnnotation(
          Integer.parseInt(tsvField.getRawView(), 0, tsvField.getRawView().length(), 10));
    }
  }
}
