package org.molgenis.vcf.annotate.vcf;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import org.molgenis.vcf.annotate.util.CharArrayBuffer;

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
      for (String line : vcfHeader.lines()) {
        writer.write(line);
        writer.write('\n');
      }
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

  public static VcfWriter create(OutputStream outputStream) {
    final int outputStreamWriterBufferSize = 32768;

    Writer writer;
    try {
      writer =
          new OutputStreamWriter(
              new GZIPOutputStream(outputStream, outputStreamWriterBufferSize) {
                {
                  def = new Deflater(Deflater.BEST_SPEED, true); // hack: set protected 'def' field
                }
              });
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new VcfWriter(writer);
  }

  @Override
  public void close() throws Exception {
    writer.close();
  }
}
