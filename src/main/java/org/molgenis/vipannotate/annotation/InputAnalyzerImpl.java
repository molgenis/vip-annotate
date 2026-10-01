package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.FieldAccessor;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.Record;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor
public class InputAnalyzerImpl<F extends Field, R extends Record<F>> implements InputAnalyzer {
  private final RecordReader<F, R> recordReader;
  private final FieldResolver<F, R> fieldResolver;
  private final SequenceVariantTypeAnalyzer<F, R> sequenceVariantTypeAnalyzer;
  private final FieldAnalyzerFactory<F> fieldAnalyzerFactory;

  private record AnnotationBinding<F extends Field, R extends Record<F>>(
      String annotationId, FieldAnalyzer<F> analyzer, FieldAccessor<F, R> accessor) {}

  @Override
  public InputAnalyses analyze(AnnotationSpecs annotationSpecs) {
    List<AnnotationBinding<F, R>> annotationBindings = createAnnotationBindings(annotationSpecs);

    R record = recordReader.read();
    if (record == null) {
      throw new IllegalArgumentException("input contains no records");
    }

    do {
      analyzeRecord(record, annotationBindings);
    } while (recordReader.readInto(record));

    return new InputAnalyses(
        sequenceVariantTypeAnalyzer.collect(), collectFieldAnalysis(annotationBindings));
  }

  private void analyzeRecord(R record, List<AnnotationBinding<F, R>> analyses) {
    for (AnnotationBinding<F, R> analysis : analyses) {
      analysis.analyzer().analyze(analysis.accessor().get(record));
    }
    sequenceVariantTypeAnalyzer.analyze(record);
  }

  private static <F extends Field, R extends Record<F>>
      Map<String, FieldAnalysis> collectFieldAnalysis(
          List<AnnotationBinding<F, R>> annotationBindings) {
    Map<String, FieldAnalysis> annotationAnalyses =
        Maps.newLinkedHashMapWithExpectedSize(annotationBindings.size());
    for (AnnotationBinding<F, R> analysis : annotationBindings) {
      FieldAnalysis fieldAnalysis = analysis.analyzer().collect();
      annotationAnalyses.put(analysis.annotationId(), fieldAnalysis);
    }
    return annotationAnalyses;
  }

  private List<AnnotationBinding<F, R>> createAnnotationBindings(AnnotationSpecs annotationSpecs) {
    List<AnnotationBinding<F, R>> annotationBindings = new ArrayList<>(annotationSpecs.size());

    annotationSpecs.forEach(
        (annotationId, annotationSpec) ->
            annotationBindings.add(
                new AnnotationBinding<>(
                    annotationId,
                    fieldAnalyzerFactory.create(annotationSpec),
                    fieldResolver.resolve(annotationId))));

    return annotationBindings;
  }

  @Override
  public void close() {
    ClosableUtils.close(recordReader);
  }
}
