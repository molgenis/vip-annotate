package org.molgenis.vipannotate.format.bed;

import java.util.List;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

public abstract class BedField extends Field {
  protected BedField(StringView fieldRawView) {
    super(fieldRawView);
  }

  public void parseValuesAppendTo(List<String> values) {
    if (isMissingValue()) {
      return;
    }

    CharSequence raw = getRawView();
    int start = 0;
    int length = raw.length();

    for (int i = 0; i < length; i++) {
      if (raw.charAt(i) == ',') {
        values.add(raw.subSequence(start, i).toString());
        start = i + 1;
      }
    }

    values.add(raw.subSequence(start, length).toString());
  }
}
