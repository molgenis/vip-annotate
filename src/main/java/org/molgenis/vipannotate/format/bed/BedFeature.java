package org.molgenis.vipannotate.format.bed;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.molgenis.vipannotate.format.Record;
import org.molgenis.vipannotate.format.StringView;

// FIXME missing bed fields after score
/** low memory, high performance, reusable, lazy parsing */
@ToString(includeFieldNames = false)
@RequiredArgsConstructor
public final class BedFeature implements Record<BedField> {
  static final int INDEX_CHROM = 0;
  static final int INDEX_CHROM_START = 1;
  static final int INDEX_CHROM_END = 2;
  static final int INDEX_NAME = 3;
  static final int INDEX_SCORE = 4;

  private final BedField[] fields;

  public BedFeature(CharSequence dataLine) {
    this.fields = new BedField[5];
    this.fields[0] = Chrom.wrap(new StringView(dataLine));
    this.fields[1] = ChromStart.wrap(new StringView(dataLine));
    this.fields[2] = ChromEnd.wrap(new StringView(dataLine));
    this.fields[3] = Name.wrap(new StringView(dataLine));
    this.fields[4] = Score.wrap(new StringView(dataLine));

    reset(dataLine);
  }

  @Override
  public BedField[] fields() {
    return fields;
  }

  @Override
  public BedField field(int index) {
    return fields[index];
  }

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

  public Score getScore() {
    return (Score) fields[INDEX_SCORE];
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

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getScore().reset(dataLine, fromIndex, toIndex);
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
      getScore().write(writer);
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
