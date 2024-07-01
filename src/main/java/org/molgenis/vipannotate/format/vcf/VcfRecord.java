package org.molgenis.vipannotate.format.vcf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/** low memory, high performance, reusable, lazy parsing */
@ToString(includeFieldNames = false)
@RequiredArgsConstructor
public final class VcfRecord {
  static final int INDEX_CHROM = 0;
  static final int INDEX_POS = 1;
  static final int INDEX_ID = 2;
  static final int INDEX_REF = 3;
  static final int INDEX_ALT = 4;
  static final int INDEX_QUAL = 5;
  static final int INDEX_FILTER = 6;
  static final int INDEX_INFO = 7;
  static final int INDEX_GENOTYPE = 8;

  private final Field[] vcfRecordFields;

  public Chrom getChrom() {
    return (Chrom) vcfRecordFields[INDEX_CHROM];
  }

  public Pos getPos() {
    return (Pos) vcfRecordFields[INDEX_POS];
  }

  public Id getId() {
    return (Id) vcfRecordFields[INDEX_ID];
  }

  public Ref getRef() {
    return (Ref) vcfRecordFields[INDEX_REF];
  }

  public Alt getAlt() {
    return (Alt) vcfRecordFields[INDEX_ALT];
  }

  public Qual getQual() {
    return (Qual) vcfRecordFields[INDEX_QUAL];
  }

  public Filter getFilter() {
    return (Filter) vcfRecordFields[INDEX_FILTER];
  }

  public Info getInfo() {
    return (Info) vcfRecordFields[INDEX_INFO];
  }

  public @Nullable Genotype getGenotype() {
    return vcfRecordFields.length == 9 ? (Genotype) vcfRecordFields[INDEX_GENOTYPE] : null;
  }

  public void reset(CharSequence dataLine) {
    int fromIndex = 0;
    int toIndex = nextTabSeparator(dataLine, fromIndex);
    getChrom().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getPos().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getId().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getRef().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getAlt().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getQual().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    toIndex = nextTabSeparator(dataLine, fromIndex);
    getFilter().reset(dataLine, fromIndex, toIndex);

    fromIndex = toIndex + 1;
    Genotype genotype = getGenotype();
    if (genotype == null) {
      getInfo().reset(dataLine, fromIndex, dataLine.length());
    } else {
      toIndex = StringView.indexOf(dataLine, '\t', fromIndex);
      getInfo().reset(dataLine, fromIndex, toIndex);

      fromIndex = toIndex + 1;
      genotype.reset(dataLine, fromIndex, dataLine.length());
    }
  }

  public void write(Writer writer) {
    try {
      getChrom().write(writer);
      writer.write('\t');
      getPos().write(writer);
      writer.write('\t');
      getId().write(writer);
      writer.write('\t');
      getRef().write(writer);
      writer.write('\t');
      getAlt().write(writer);
      writer.write('\t');
      getQual().write(writer);
      writer.write('\t');
      getFilter().write(writer);
      writer.write('\t');
      getInfo().write(writer);

      Genotype genotype = getGenotype();
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
