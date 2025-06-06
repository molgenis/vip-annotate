package org.molgenis.vipannotate.vcf;

import static java.util.Objects.requireNonNull;

import java.io.*;
import org.molgenis.vipannotate.util.CharArrayBuffer;

// TODO write bgzip instead of gzip, see
// https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/util/BlockCompressedOutputStream.java
public class VcfWriter implements AutoCloseable {
  private final Writer writer;
  private final CharArrayBuffer reusableCharArrayBuffer;

  public VcfWriter(Writer writer) {
    this.writer = requireNonNull(writer);
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
    try {
      for (String token : vcfRecord.tokens()) {
        writer.write(token);
        writer.write('\t');
      }
      writer.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void write(Iterable<VcfRecord> vcfRecords) {
    // zero-copy, significantly faster than BufferedWriter
    reusableCharArrayBuffer.clear();

    for (VcfRecord vcfRecord : vcfRecords) {
      for (String token : vcfRecord.tokens()) {
        reusableCharArrayBuffer.append(token);
        reusableCharArrayBuffer.append('\t');
      }
      reusableCharArrayBuffer.append('\n');
    }

    try {
      writer.write(reusableCharArrayBuffer.getBuffer(), 0, reusableCharArrayBuffer.getLength());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
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
