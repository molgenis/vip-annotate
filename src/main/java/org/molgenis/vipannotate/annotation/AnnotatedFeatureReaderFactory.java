package org.molgenis.vipannotate.annotation;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.format.bed.BedFeature;
import org.molgenis.vipannotate.format.bed.BedField;
import org.molgenis.vipannotate.format.bed.BedParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.util.Input;

@RequiredArgsConstructor
public class AnnotatedFeatureReaderFactory {

  public AnnotatedFeatureReader create(
      Input input, InputFormat inputFormat, ResolvedAnnotationSpecs annotationSpecs) {
    return switch (inputFormat) {
      case BedInputFormat bedInputFormat -> create(input, bedInputFormat, annotationSpecs);
      case TsvInputFormat tsvInputFormat -> create(input, tsvInputFormat, annotationSpecs);
      case VcfInputFormat vcfInputFormat -> create(input, vcfInputFormat, annotationSpecs);
    };
  }

  private VcfAnnotatedFeatureReader create(
      Input input, VcfInputFormat vcfInputFormat, ResolvedAnnotationSpecs annotationSpecs) {
    return new VcfAnnotatedFeatureReader(input, vcfInputFormat, annotationSpecs);
  }

  private AnnotatedFeatureReader create(
      Input input, TsvInputFormat tsvInputFormat, ResolvedAnnotationSpecs annotationSpecs) {

    Function<TsvRecord, AnnotatedFeature<?, ?>> mapper =
        switch (tsvInputFormat.annotationType()) {
          case INTERVAL -> throw new UnsupportedOperationException(); // FIXME
          case POSITION -> new TsvAnnotatedPositionMapper(tsvInputFormat, annotationSpecs)::apply;
          case SEQUENCE_VARIANT ->
              new TsvAnnotatedSequenceVariantMapper(tsvInputFormat, annotationSpecs)::apply;
        };
    return new AnnotatedFeatureReaderImpl<>(TsvParserFactory.create(input), mapper);
  }

  private static AnnotatedFeatureReader create(
      Input input, BedInputFormat bedInputFormat, ResolvedAnnotationSpecs annotationSpecs) {
    RecordReader<BedField, BedFeature> bedParser = BedParserFactory.create(input);
    Function<BedFeature, AnnotatedFeature<?, ?>> mapper =
        new BedAnnotatedPositionMapper(bedInputFormat, annotationSpecs)::apply;
    return new AnnotatedFeatureReaderImpl<>(bedParser, mapper);
  }

  public static AnnotatedFeatureReaderFactory create() {
    return new AnnotatedFeatureReaderFactory();
  }
}
