package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.BedInputFormat;
import org.molgenis.vipannotate.annotation.spec.InputFormat;
import org.molgenis.vipannotate.annotation.spec.TsvInputFormat;
import org.molgenis.vipannotate.annotation.spec.VcfInputFormat;

public class AnnotationAnalyzerFactory {

  public AnnotationsAnalyzer create(InputFormat inputFormat) {
    return switch (inputFormat) {
      case BedInputFormat bedInputFormat -> new BedAnnotationsAnalyzer(bedInputFormat);
      case TsvInputFormat tsvInputFormat -> new TsvAnnotationsAnalyzer(tsvInputFormat);
      case VcfInputFormat vcfInputFormat -> new VcfAnnotationsAnalyzer(vcfInputFormat);
    };
  }

  public static AnnotationAnalyzerFactory create() {
    return new AnnotationAnalyzerFactory();
  }
}
