package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.nio.file.Path;
import java.util.EnumSet;
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
    RecordReader<TsvField, TsvRecord> recordReader = TsvParserFactory.createFromPath(input);
    FieldResolver<TsvField, TsvRecord> fieldResolver =
        annotationId -> {
          Integer fieldIndex = inputFormat.annotations().get(annotationId);

          if (fieldIndex == null) {
            throw new IllegalArgumentException(
                "'schema.annotation_datasets.%s' not defined in 'input.annotations'"
                    .formatted(annotationId));
          }

          return record -> record.fields()[fieldIndex];
        };
    SequenceVariantTypeAnalyzer<TsvField, TsvRecord> sequenceVariantTypeAnalyzer;
    Integer refIndex = inputFormat.ref();
    Integer altIndex = inputFormat.alt();
    if (refIndex != null && altIndex != null) {
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

  private InputAnalyzer createVcf(Path input, VcfInputFormat inputFormat) {
    throw new UnsupportedOperationException("Not supported yet."); // FIXME
  }

  public static InputAnalyzerFactory create() {
    return new InputAnalyzerFactory();
  }
}
