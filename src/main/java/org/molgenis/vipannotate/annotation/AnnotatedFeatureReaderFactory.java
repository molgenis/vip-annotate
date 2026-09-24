package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
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

@RequiredArgsConstructor
public class AnnotatedFeatureReaderFactory {

  public AnnotatedFeatureReader create(
      Path input, InputFormat inputFormat, ResolvedAnnotationSpecs annotationSpecs) {
    return switch (inputFormat) {
      case BedInputFormat bedInputFormat -> createFromBed(input, bedInputFormat, annotationSpecs);
      case TsvInputFormat tsvInputFormat -> createFromTsv(input, tsvInputFormat, annotationSpecs);
      case VcfInputFormat vcfInputFormat -> createFromVcf(input, vcfInputFormat, annotationSpecs);
    };
  }

  private VcfAnnotatedFeatureReader createFromVcf(
      Path input, VcfInputFormat vcfInputFormat, ResolvedAnnotationSpecs annotationSpecs) {
    return new VcfAnnotatedFeatureReader(input, vcfInputFormat, annotationSpecs);
  }

  private AnnotatedFeatureReader createFromTsv(
      Path input, TsvInputFormat tsvInputFormat, ResolvedAnnotationSpecs annotationSpecs) {

    Function<TsvRecord, AnnotatedFeature<?, ?>> mapper =
        switch (tsvInputFormat.annotationType()) {
          case INTERVAL -> throw new UnsupportedOperationException(); // FIXME
          case POSITION -> new TsvAnnotatedPositionMapper(tsvInputFormat, annotationSpecs)::apply;
          case SEQUENCE_VARIANT ->
              new TsvAnnotatedSequenceVariantMapper(tsvInputFormat, annotationSpecs)::apply;
        };
    return new AnnotatedFeatureReaderImpl<>(TsvParserFactory.createFromPath(input), mapper);
  }

  private static AnnotatedFeatureReader createFromBed(
      Path input, BedInputFormat bedInputFormat, ResolvedAnnotationSpecs annotationSpecs) {
    RecordReader<BedField, BedFeature> bedParser = BedParserFactory.createFromPath(input);
    Function<BedFeature, AnnotatedFeature<?, ?>> mapper =
        new BedAnnotatedPositionMapper(bedInputFormat, annotationSpecs)::apply;
    return new AnnotatedFeatureReaderImpl<>(bedParser, mapper);
  }

  public static AnnotatedFeatureReaderFactory create() {
    return new AnnotatedFeatureReaderFactory();
  }
}
