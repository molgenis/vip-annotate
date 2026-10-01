package org.molgenis.vipannotate.format.tsv;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import lombok.ToString;
import org.molgenis.vipannotate.format.Record;
import org.molgenis.vipannotate.format.StringView;
import org.molgenis.vipannotate.format.vcf.*;

/** low memory, high performance, reusable, lazy parsing */
@ToString
public final class TsvRecord implements Record<TsvField> {
  private final TsvField[] fields;

  public TsvRecord(CharSequence dataLine) {
    int nrFields = countTabs(dataLine) + 1;
    this.fields = new TsvField[nrFields];
    for (int i = 0; i < nrFields; i++) {
      this.fields[i] = TsvField.wrap(new StringView(dataLine));
    }
    reset(dataLine);
  }

  @Override
  public TsvField[] fields() {
    return fields;
  }

  @Override
  public TsvField field(int index) {
    return fields[index];
  }

  public void reset(CharSequence dataLine) {
    int fromIndex = 0;

    for (int i = 0; i < fields.length - 1; i++) {
      int toIndex = StringView.indexOf(dataLine, '\t', fromIndex);
      if (toIndex == -1) {
        throw new IllegalArgumentException();
      }
      fields[i].reset(dataLine, fromIndex, toIndex);
      fromIndex = toIndex + 1;
    }

    // validate correct number of fields
    if (StringView.indexOf(dataLine, '\t', fromIndex) != -1) {
      throw new IllegalArgumentException();
    }
    fields[fields.length - 1].reset(dataLine, fromIndex, dataLine.length());
  }

  public void write(Writer writer) {
    try {
      for (int i = 0; i < fields.length; i++) {
        if (i > 0) {
          writer.write('\t');
        }
        fields[i].write(writer);
      }
      writer.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static int countTabs(CharSequence dataLine) {
    int count = 0;
    for (int i = 0, length = dataLine.length(); i < length; i++) {
      if (dataLine.charAt(i) == '\t') {
        count++;
      }
    }
    return count;
  }
}
