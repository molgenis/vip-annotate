package org.molgenis.vipannotate.format.vcf;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

public abstract class VcfField extends Field {
  protected VcfField(StringView fieldRawView) {
    super(fieldRawView);
  }
}
