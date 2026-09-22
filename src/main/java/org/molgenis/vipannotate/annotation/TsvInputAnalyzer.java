package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.annotation.SequenceVariantType.OTHER;
import static org.molgenis.vipannotate.annotation.SequenceVariantType.STRUCTURAL;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.tsv.TsvParser;
import org.molgenis.vipannotate.format.tsv.TsvParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.format.vcf.AltAlleleRegistry;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor
public class TsvInputAnalyzer implements InputAnalyzer {
  private final TsvInputFormat tsvInputFormat;

  private record TsvAnnotationAnalysis(
      int fieldIndex, String annotationDatasetId, FieldAnalyzer<Field> analyzer) {}

  @Override
  public InputAnalyses analyze(Input input, AnnotationSpecs annotationSpecs) {
    TsvAnnotationAnalysis[] analyses = createAnalyses(annotationSpecs);

    Integer refIndex = tsvInputFormat.ref();
    Integer altIndex = tsvInputFormat.alt();
    boolean collectSequenceVariantTypes = refIndex != null && altIndex != null;

    // process records
    // FIXME rethink complementOf default
    EnumSet<SequenceVariantType> sequenceVariantTypes =
        collectSequenceVariantTypes
            ? EnumSet.noneOf(SequenceVariantType.class)
            : EnumSet.complementOf(EnumSet.of(STRUCTURAL, OTHER));

    try (TsvParser tsvParser = TsvParserFactory.create(input)) {
      TsvRecord tsvRecord = tsvParser.read();
      if (tsvRecord == null) {
        throw new IllegalArgumentException("tsv contains no records");
      }

      do {
        for (TsvAnnotationAnalysis analysis : analyses) {
          analysis.analyzer().analyze(tsvRecord.fields()[analysis.fieldIndex()]);
        }
        if (collectSequenceVariantTypes) {
          int refLen = tsvRecord.fields()[refIndex].getRawView().length();
          AltAllele altAllele =
              AltAlleleRegistry.INSTANCE.getOrWrap(tsvRecord.fields()[altIndex].getRawView());
          sequenceVariantTypes.add(SequenceVariantTypeDetector.determineType(refLen, altAllele));
        }
      } while (tsvParser.readInto(tsvRecord));
    }

    // create analyses
    Map<String, FieldAnalysis> annotationAnalysesMap =
        Maps.newLinkedHashMapWithExpectedSize(analyses.length);
    for (TsvAnnotationAnalysis analysis : analyses) {
      FieldAnalysis fieldAnalysis = analysis.analyzer().collect();
      annotationAnalysesMap.put(analysis.annotationDatasetId(), fieldAnalysis);
    }

    return new InputAnalyses(sequenceVariantTypes, annotationAnalysesMap);
  }

  private TsvAnnotationAnalysis[] createAnalyses(AnnotationSpecs annotationSpecs) {
    Map<String, Integer> tsvAnnotations = tsvInputFormat.annotations();

    AtomicInteger atomicInteger = new AtomicInteger(0);
    TsvAnnotationAnalysis[] analyses = new TsvAnnotationAnalysis[annotationSpecs.size()];
    annotationSpecs.forEach(
        (annotationId, annotationSpec) -> {
          Integer fieldIndex = tsvAnnotations.get(annotationId);
          if (fieldIndex == null) {
            throw new IllegalArgumentException(
                "'schema.annotation_datasets.%s' not defined in 'input.annotations'"
                    .formatted(annotationId));
          }

          analyses[atomicInteger.getAndIncrement()] =
              new TsvAnnotationAnalysis(fieldIndex, annotationId, createAnalyzer(annotationSpec));
        });
    return analyses;
  }

  private static FieldAnalyzer<Field> createAnalyzer(AnnotationSpec annotationSpec) {
    return switch (annotationSpec) {
      case EnumAnnotationSpec spec -> new EnumFieldAnalyzer(spec);
      case EnumSetAnnotationSpec spec -> new EnumSetFieldAnalyzer(spec);
      case FloatAnnotationSpec spec -> new FloatFieldAnalyzer(spec);
      case IntAnnotationSpec spec -> new IntFieldAnalyzer(spec);
    };
  }
}
