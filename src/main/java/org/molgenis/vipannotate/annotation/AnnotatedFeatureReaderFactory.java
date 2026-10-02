package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.format.bed.BedFeature;
import org.molgenis.vipannotate.format.bed.BedField;
import org.molgenis.vipannotate.format.bed.BedParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvField;
import org.molgenis.vipannotate.format.tsv.TsvParser;
import org.molgenis.vipannotate.format.tsv.TsvParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.util.Maps;

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
    String missingValue = tsvInputFormat.missingValue();
    TsvParser tsvParser =
        TsvParserFactory.createFromPath(input, missingValue != null ? missingValue : "");

    Map<String, Integer> header = tsvInputFormat.header() ? readHeaderIndices(tsvParser) : Map.of();
    ResolvedTsvColumns resolvedColumns = resolveColumns(tsvInputFormat.columns(), header);

    Function<TsvRecord, AnnotatedFeature<?, ?>> mapper =
        switch (tsvInputFormat.annotationType()) {
          case INTERVAL -> throw new UnsupportedOperationException(); // FIXME
          case POSITION ->
              // FIXME hardcoded contigIndex and startIndex
              new TsvAnnotatedPositionMapper(
                      tsvInputFormat.coordinateSystem(), resolvedColumns, annotationSpecs)
                  ::apply;
          case SEQUENCE_VARIANT ->
              // FIXME hardcoded contigIndex, startIndex, refIndex and altIndex
              new TsvAnnotatedSequenceVariantMapper(
                      tsvInputFormat.coordinateSystem(), resolvedColumns, annotationSpecs)
                  ::apply;
        };
    return new AnnotatedFeatureReaderImpl<>(tsvParser, mapper);
  }

  public record ResolvedTsvColumns(
      int contig,
      int start,
      @Nullable Integer end,
      @Nullable Integer ref,
      @Nullable Integer alt,
      Map<String, Integer> annotations) {}

  private Map<String, Integer> readHeaderIndices(TsvParser tsvParser) {
    TsvRecord headerRecord = tsvParser.read();
    if (headerRecord == null) {
      throw new IllegalArgumentException("empty tsv, expected header");
    }

    TsvField[] fields = headerRecord.fields();
    Map<String, Integer> indices = Maps.newHashMapWithExpectedSize(fields.length);
    for (int i = 0; i < fields.length; i++) {
      String name = fields[i].getRawView().toString();
      if (indices.put(name, i) != null) {
        throw new IllegalArgumentException("duplicate tsv col header '%s'".formatted(name));
      }
    }
    return indices;
  }

  private ResolvedTsvColumns resolveColumns(
      TsvColumns columns, Map<String, Integer> headerIndices) {
    int contig = resolveColumnIndex(columns.contig(), headerIndices);
    int start = resolveColumnIndex(columns.start(), headerIndices);
    Integer end = columns.end() != null ? resolveColumnIndex(columns.end(), headerIndices) : null;
    Integer ref = columns.ref() != null ? resolveColumnIndex(columns.ref(), headerIndices) : null;
    Integer alt = columns.alt() != null ? resolveColumnIndex(columns.alt(), headerIndices) : null;

    Map<String, Integer> annotations =
        Maps.newHashMapWithExpectedSize(columns.annotations().size());
    columns
        .annotations()
        .forEach(
            (annotationId, column) ->
                annotations.put(annotationId, resolveColumnIndex(column, headerIndices)));

    return new ResolvedTsvColumns(contig, start, end, ref, alt, annotations);
  }

  private int resolveColumnIndex(TsvColumn column, Map<String, Integer> headerIndices) {
    if (column.index() != null) {
      return column.index() - 1;
    }

    Integer index = headerIndices.get(column.name());
    if (index == null) {
      throw new IllegalArgumentException(
          "tsv column '%s' not found in header".formatted(column.name()));
    }
    return index;
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
