package org.molgenis.vipannotate.format.bed;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;
import org.molgenis.vipannotate.format.vcf.*;

/** low memory, high performance, reusable, lazy parsing */
@ToString(includeFieldNames = false)
@RequiredArgsConstructor
public final class BedFeature {
  static final int INDEX_CHROM = 0;
  static final int INDEX_CHROM_START = 1;
  static final int INDEX_CHROM_END = 2;
  static final int INDEX_NAME = 3;

  private final Field[] fields;

  public Chrom getChrom() {
    return (Chrom) fields[INDEX_CHROM];
  }

  public ChromStart getChromStart() {
    return (ChromStart) fields[INDEX_CHROM_START];
  }

  public ChromEnd getChromEnd() {
    return (ChromEnd) fields[INDEX_CHROM_END];
  }

  public Name getName() {
    return (Name) fields[INDEX_NAME];
  }

  public void reset(CharSequence dataLine) {
    int fromIndex = 0;
    int toIndex = nextTabSeparator(dataLine, fromIndex);
    getChrom().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getChromStart().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getChromEnd().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getName().reset(dataLine, fromIndex, toIndex);
  }

  public void write(Writer writer) {
    try {
      getChrom().write(writer);
      writer.write('\t');
      getChromStart().write(writer);
      writer.write('\t');
      getChromEnd().write(writer);
      writer.write('\t');
      getName().write(writer);
      writer.write('\t');

      writer.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static int nextTabSeparator(CharSequence line, int fromIndex) {
    int toIndex = StringView.indexOf(line, '\t', fromIndex);
    if (toIndex == -1) {
      toIndex = line.length();
    }
    return toIndex;
  }
}
