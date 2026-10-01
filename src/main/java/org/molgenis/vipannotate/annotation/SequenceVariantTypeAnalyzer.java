package org.molgenis.vipannotate.annotation;

import java.util.EnumSet;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.Record;

public interface SequenceVariantTypeAnalyzer<F extends Field, R extends Record<F>> {
  void analyze(R record);

  EnumSet<SequenceVariantType> collect();
}
