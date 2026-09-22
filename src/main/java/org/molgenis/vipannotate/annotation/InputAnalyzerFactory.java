package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.BedInputFormat;
import org.molgenis.vipannotate.annotation.spec.InputFormat;
import org.molgenis.vipannotate.annotation.spec.TsvInputFormat;
import org.molgenis.vipannotate.annotation.spec.VcfInputFormat;

public class InputAnalyzerFactory {

  public InputAnalyzer create(InputFormat inputFormat) {
    return switch (inputFormat) {
      case BedInputFormat bedInputFormat -> new BedInputAnalyzer(bedInputFormat);
      case TsvInputFormat tsvInputFormat -> new TsvInputAnalyzer(tsvInputFormat);
      case VcfInputFormat vcfInputFormat -> new VcfInputAnalyzer(vcfInputFormat);
    };
  }

  public static InputAnalyzerFactory create() {
    return new InputAnalyzerFactory();
  }
}
