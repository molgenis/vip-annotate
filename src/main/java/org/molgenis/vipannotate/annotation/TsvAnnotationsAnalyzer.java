package org.molgenis.vipannotate.annotation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.tsv.TsvParser;
import org.molgenis.vipannotate.format.tsv.TsvParserFactory;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor
public class TsvAnnotationsAnalyzer implements AnnotationsAnalyzer {
  private final TsvInputFormat tsvInputFormat;

  private record TsvAnnotationAnalysis(
      int fieldIndex, String annotationDatasetId, AnnotationAnalyzer<Field> analyzer) {}

  @Override
  public AnnotationAnalyses analyze(Input input, AnnotationSpecs annotationSpecs) {
    TsvAnnotationAnalysis[] analyses = createAnalyses(annotationSpecs);

    // process records
    try (TsvParser tsvParser = TsvParserFactory.create(input)) {
      TsvRecord tsvRecord = tsvParser.read();
      if (tsvRecord == null) {
        throw new IllegalArgumentException("tsv contains no records");
      }

      do {
        for (TsvAnnotationAnalysis analysis : analyses) {
          analysis.analyzer().analyze(tsvRecord.fields()[analysis.fieldIndex()]);
        }
      } while (tsvParser.readInto(tsvRecord));
    }

    // create analyses
    Map<String, AnnotationAnalysis> annotationAnalysesMap =
        Maps.newLinkedHashMapWithExpectedSize(analyses.length);
    for (TsvAnnotationAnalysis analysis : analyses) {
      AnnotationAnalysis annotationAnalysis = analysis.analyzer().collect();
      annotationAnalysesMap.put(analysis.annotationDatasetId(), annotationAnalysis);
    }
    return new AnnotationAnalyses(annotationAnalysesMap);
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

  private static AnnotationAnalyzer<Field> createAnalyzer(AnnotationSpec annotationSpec) {
    return switch (annotationSpec) {
      case EnumAnnotationSpec spec -> new EnumAnnotationAnalyzer(spec);
      case EnumSetAnnotationSpec spec -> new EnumSetAnnotationAnalyzer(spec);
      case FloatAnnotationSpec spec -> new FloatAnnotationAnalyzer(spec);
      case IntAnnotationSpec spec -> new IntAnnotationAnalyzer(spec);
    };
  }
}
