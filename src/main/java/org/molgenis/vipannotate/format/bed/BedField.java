package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

public abstract class BedField extends Field {
  protected BedField(StringView fieldRawView) {
    super(fieldRawView);
  }
}
