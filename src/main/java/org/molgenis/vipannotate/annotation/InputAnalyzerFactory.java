package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.format.bed.BedFeature;
import org.molgenis.vipannotate.format.bed.BedField;
import org.molgenis.vipannotate.format.bed.BedParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvField;
import org.molgenis.vipannotate.format.tsv.TsvParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.format.vcf.AltAlleleRegistry;
import org.molgenis.vipannotate.util.Maps;

public class InputAnalyzerFactory {

  public InputAnalyzer create(Path input, InputFormat inputFormat) {
    return switch (inputFormat) {
      case BedInputFormat bedInputFormat -> createBed(input, bedInputFormat);
      case TsvInputFormat tsvInputFormat -> createTsv(input, tsvInputFormat);
      case VcfInputFormat vcfInputFormat -> createVcf(input, vcfInputFormat);
    };
  }

  private InputAnalyzer createBed(Path input, BedInputFormat inputFormat) {
    RecordReader<BedField, BedFeature> recordReader = BedParserFactory.createFromPath(input);
    FieldResolver<BedField, BedFeature> fieldResolver =
        annotationId -> {
          BedFieldType fieldIndex = inputFormat.annotations().get(annotationId);

          if (fieldIndex == null) {
            throw new IllegalArgumentException(
                "'schema.annotation_datasets.%s' not defined in 'input.annotations'"
                    .formatted(annotationId));
          }

          return record -> record.fields()[fieldIndex.getColIndex()];
        };

    // FIXME is this always true?
    SequenceVariantTypeAnalyzer<BedField, BedFeature> sequenceVariantTypeAnalyzer =
        new SequenceVariantTypeAnalyzer<>() {
          @Override
          public void analyze(BedFeature bedFeature) {}

          @Override
          public EnumSet<SequenceVariantType> collect() {
            return EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER));
          }
        };
    // FIXME return field analyzer factory that is aware of type of bed columns
    return new InputAnalyzerImpl<>(
        recordReader, fieldResolver, sequenceVariantTypeAnalyzer, new FieldAnalyzerFactory<>());
  }

  private InputAnalyzer createTsv(Path input, TsvInputFormat inputFormat) {
    String missingValue = inputFormat.missingValue() != null ? inputFormat.missingValue() : "";
    char listSeparator = inputFormat.listSeparator() != null ? inputFormat.listSeparator() : ',';
    RecordReader<TsvField, TsvRecord> recordReader =
        TsvParserFactory.createFromPath(input, missingValue, listSeparator);
    // create column name-index map for tsv with header
    Map<String, Integer> tsvHeaderMap;
    if (inputFormat.header()) {
      TsvRecord tsvRecord = recordReader.read();
      if (tsvRecord == null) {
        throw new IllegalArgumentException(); // TODO add msg
      }
      TsvField[] tsvHeader = tsvRecord.fields();
      tsvHeaderMap = Maps.newHashMapWithExpectedSize(tsvHeader.length);
      for (int i = 0; i < tsvHeader.length; i++) {
        tsvHeaderMap.put(tsvHeader[i].getRawView().toString(), i);
      }
    } else {
      tsvHeaderMap = null;
    }

    FieldResolver<TsvField, TsvRecord> fieldResolver =
        annotationId -> {
          TsvColumn tsvColumn = inputFormat.columns().annotations().get(annotationId);

          if (tsvColumn == null) {
            throw new IllegalArgumentException(
                "'definition.annotations.%s' not defined in 'input.colums.annotations'"
                    .formatted(annotationId));
          }

          int index = getTsvColumnIndex(tsvColumn, tsvHeaderMap);
          return record -> record.fields()[index];
        };
    SequenceVariantTypeAnalyzer<TsvField, TsvRecord> sequenceVariantTypeAnalyzer;
    TsvColumn refColumn = inputFormat.columns().ref();
    TsvColumn altColumn = inputFormat.columns().alt();
    if (refColumn != null && altColumn != null) {
      int refIndex = getTsvColumnIndex(refColumn, tsvHeaderMap);
      int altIndex = getTsvColumnIndex(altColumn, tsvHeaderMap);
      sequenceVariantTypeAnalyzer =
          new SequenceVariantTypeAnalyzer<>() {
            private final EnumSet<SequenceVariantType> sequenceVariantTypes =
                EnumSet.noneOf(SequenceVariantType.class);

            @Override
            public void analyze(TsvRecord record) {
              int refLen = record.fields()[refIndex].getRawView().length();
              AltAllele altAllele =
                  AltAlleleRegistry.INSTANCE.getOrWrap(record.fields()[altIndex].getRawView());
              sequenceVariantTypes.add(
                  SequenceVariantTypeDetector.determineType(refLen, altAllele));
            }

            @Override
            public EnumSet<SequenceVariantType> collect() {
              return sequenceVariantTypes;
            }
          };

    } else {
      sequenceVariantTypeAnalyzer =
          new SequenceVariantTypeAnalyzer<>() {
            @Override
            public void analyze(TsvRecord record) {}

            @Override
            public EnumSet<SequenceVariantType> collect() {
              return EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER));
            }
          };
    }
    return new InputAnalyzerImpl<>(
        recordReader, fieldResolver, sequenceVariantTypeAnalyzer, new FieldAnalyzerFactory<>());
  }

  // TODO dedup using AnnotatedFeaturedReaderFactory.ResolvedTsvColumns
  private int getTsvColumnIndex(TsvColumn tsvColumn, @Nullable Map<String, Integer> tsvHeaderMap) {
    int index;
    if (tsvColumn.index() != null) {
      index = tsvColumn.index() - 1;
    } else {
      if (tsvHeaderMap == null) {
        throw new IllegalArgumentException();
      }
      index = tsvHeaderMap.getOrDefault(tsvColumn.name(), -1);
      if (index == -1) {
        throw new IllegalArgumentException();
      }
    }
    return index;
  }

  private InputAnalyzer createVcf(Path input, VcfInputFormat inputFormat) {
    throw new UnsupportedOperationException("Not supported yet."); // FIXME
  }

  public static InputAnalyzerFactory create() {
    return new InputAnalyzerFactory();
  }
}
