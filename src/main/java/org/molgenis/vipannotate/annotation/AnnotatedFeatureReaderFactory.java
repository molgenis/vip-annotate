package org.molgenis.vipannotate.annotation;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.*;
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

  private TsvAnnotatedFeatureReader create(
      Input input, TsvInputFormat tsvInputFormat, ResolvedAnnotationSpecs annotationSpecs) {

    Function<TsvRecord, AnnotatedFeature<?, ?>> mapper =
        switch (tsvInputFormat.annotationType()) {
          case INTERVAL -> throw new UnsupportedOperationException(); // FIXME
          case POSITION -> new TsvAnnotatedPositionMapper(tsvInputFormat, annotationSpecs)::apply;
          case SEQUENCE_VARIANT ->
              new TsvAnnotatedSequenceVariantMapper(tsvInputFormat, annotationSpecs)::apply;
        };
    return new TsvAnnotatedFeatureReader(TsvParserFactory.create(input), mapper);
  }

  private static BedAnnotatedFeatureReader create(
      Input input, BedInputFormat bedInputFormat, ResolvedAnnotationSpecs annotationSpecs) {
    return new BedAnnotatedFeatureReader(input, bedInputFormat, annotationSpecs);
  }

  public static AnnotatedFeatureReaderFactory create() {
    return new AnnotatedFeatureReaderFactory();
  }
}
