package org.molgenis.vipannotate.format.vcf;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.CharArrayBuffer;

public class VcfWriter implements AutoCloseable {
  private final Writer writer;
  private final CharArrayBuffer reusableCharArrayBuffer;

  public VcfWriter(Writer writer) {
    this.writer = writer;
    this.reusableCharArrayBuffer = new CharArrayBuffer(32768);
  }

  public void writeHeader(VcfHeader vcfHeader) {
    try {
      VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();
      for (VcfMetaInfo.Line line : vcfMetaInfo.lines()) {
        writer.write(line.line());
        writer.write('\n');
      }
      writer.write(vcfHeader.vcfHeaderLine().line());
      writer.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void write(VcfRecord vcfRecord) {
    write(List.of(vcfRecord));
  }

  public void write(Iterable<VcfRecord> vcfRecords) {
    // zero-copy, significantly faster than BufferedWriter
    reusableCharArrayBuffer.clear();

    for (VcfRecord vcfRecord : vcfRecords) {
      write(vcfRecord, reusableCharArrayBuffer);
    }

    try {
      writer.write(reusableCharArrayBuffer.getBuffer(), 0, reusableCharArrayBuffer.getLength());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void write(VcfRecord vcfRecord, CharArrayBuffer reusableCharArrayBuffer) {
    // chrom
    reusableCharArrayBuffer.append(vcfRecord.chrom());
    reusableCharArrayBuffer.append('\t');

    // pos
    reusableCharArrayBuffer.append(Long.toString(vcfRecord.pos()));
    reusableCharArrayBuffer.append('\t');

    // id
    String[] id = vcfRecord.id();
    writeStringValueList(id, reusableCharArrayBuffer, ',');
    reusableCharArrayBuffer.append('\t');

    // ref
    reusableCharArrayBuffer.append(vcfRecord.ref());
    reusableCharArrayBuffer.append('\t');

    // alt
    @Nullable String[] alt = vcfRecord.alt();
    writeStringValueList(alt, reusableCharArrayBuffer, ',');
    reusableCharArrayBuffer.append('\t');

    // qual
    String qual = vcfRecord.qual();
    writeStringValue(qual, reusableCharArrayBuffer);
    reusableCharArrayBuffer.append('\t');

    // filter
    String[] filter = vcfRecord.filter();
    writeStringValueList(filter, reusableCharArrayBuffer, ';');
    reusableCharArrayBuffer.append('\t');

    // info
    Map<String, @Nullable String> info = vcfRecord.info();
    int infoSize = info.size();
    if (infoSize == 0) {
      reusableCharArrayBuffer.append('.');
    } else if (infoSize == 1) {
      reusableCharArrayBuffer.append(info.keySet().iterator().next());
      reusableCharArrayBuffer.append('=');
      reusableCharArrayBuffer.append(info.values().iterator().next());
    } else {
      Iterator<String> keyIterator = info.keySet().iterator();
      Iterator<String> valueIterator = info.values().iterator();
      int i;
      for (i = 0; i < infoSize - 1; ++i) {
        reusableCharArrayBuffer.append(keyIterator.next());
        String value = valueIterator.next();
        if (value != null) { // value is null for flag
          reusableCharArrayBuffer.append('=');
          reusableCharArrayBuffer.append(value);
        }
        reusableCharArrayBuffer.append(';');
      }
      reusableCharArrayBuffer.append(keyIterator.next());
      reusableCharArrayBuffer.append('=');
      reusableCharArrayBuffer.append(valueIterator.next());
    }
    reusableCharArrayBuffer.append('\t');

    // format
    boolean hasGenotypeInfo = vcfRecord.sampleData() != null;
    if (hasGenotypeInfo) {
      // format
      String[] format = requireNonNull(vcfRecord.format());
      writeStringValueList(format, reusableCharArrayBuffer, ':');
      reusableCharArrayBuffer.append('\t');

      // sample data
      String[] sampleData = vcfRecord.sampleData();
      writeStringValueList(sampleData, reusableCharArrayBuffer, '\t');
    }

    // newline
    reusableCharArrayBuffer.append('\n');
  }

  private static void writeStringValue(@Nullable String token, CharArrayBuffer charArrayBuffer) {
    if (token != null) {
      charArrayBuffer.append(token);
    } else {
      charArrayBuffer.append('.');
    }
  }

  private static void writeStringValueList(
      @Nullable String[] token, CharArrayBuffer charArrayBuffer, char separator) {
    if (token.length == 0) {
      charArrayBuffer.append('.');
    } else if (token.length == 1) {
      writeStringValue(token[0], charArrayBuffer);
    } else {
      int i;
      for (i = 0; i < token.length - 1; ++i) {
        writeStringValue(token[i], charArrayBuffer);
        charArrayBuffer.append(separator);
      }
      writeStringValue(token[i], charArrayBuffer);
    }
  }

  @Override
  public void close() {
    try {
      writer.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
