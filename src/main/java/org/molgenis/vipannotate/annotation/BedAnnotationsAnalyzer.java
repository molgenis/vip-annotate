package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.BedInputFormat;
import org.molgenis.vipannotate.format.bed.BedFeature;
import org.molgenis.vipannotate.format.bed.BedParser;
import org.molgenis.vipannotate.format.bed.BedParserFactory;
import org.molgenis.vipannotate.util.Input;

@RequiredArgsConstructor
public class BedAnnotationsAnalyzer implements AnnotationsAnalyzer {
  private final BedInputFormat bedInputFormat;

  @Override
  public AnnotationAnalyses analyze(Input input, AnnotationSpecs annotationSpecs) {
    // process records
    try (BedParser bedParser = BedParserFactory.create(input)) {
      while (bedParser.hasNext()) {
        // TODO perf: reuse BedFeature (similar to TsvParser)
        BedFeature bedFeature = bedParser.next();
      }
    }
    throw new UnsupportedOperationException(); // FIXME
  }
}
