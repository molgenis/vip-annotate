package org.molgenis.vipannotate.format.vcf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.StringView;

/** Low memory, high performance, reusable, lazy parsing. */
@ToString(includeFieldNames = false)
@RequiredArgsConstructor
public final class VcfRecord {
  @Getter private final Chrom chrom;
  @Getter private final Pos pos;
  @Getter private final Id id;
  @Getter private final Ref ref;
  @Getter private final Alt alt;
  @Getter private final Qual qual;
  @Getter private final Filter filter;
  @Getter private final Info info;
  @Getter private final @Nullable Genotype genotype;

  public void reset(CharSequence dataLine) {
    int fromIndex = 0;
    int toIndex = nextTabSeparator(dataLine, fromIndex);
    chrom.reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    pos.reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    id.reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    ref.reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    alt.reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    qual.reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    filter.reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;

    if (genotype == null) {
      info.reset(dataLine, fromIndex, dataLine.length());
    } else {
      toIndex = StringView.indexOf(dataLine, '\t', fromIndex);
      info.reset(dataLine, fromIndex, toIndex);

      fromIndex = toIndex + 1;
      genotype.reset(dataLine, fromIndex, dataLine.length());
    }
  }

  public void write(Writer writer) {
    try {
      chrom.write(writer);
      writer.write('\t');

      pos.write(writer);
      writer.write('\t');

      id.write(writer);
      writer.write('\t');

      ref.write(writer);
      writer.write('\t');

      alt.write(writer);
      writer.write('\t');

      qual.write(writer);
      writer.write('\t');

      filter.write(writer);
      writer.write('\t');

      info.write(writer);
      if (genotype != null) {
        writer.write('\t');
        genotype.write(writer);
      }

      writer.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static int nextTabSeparator(CharSequence line, int fromIndex) {
    int toIndex = StringView.indexOf(line, '\t', fromIndex);
    if (toIndex == -1) {
      throw new IllegalArgumentException();
    }
    return toIndex;
  }
}
