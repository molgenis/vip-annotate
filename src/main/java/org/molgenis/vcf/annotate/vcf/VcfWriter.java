package org.molgenis.vcf.annotate.vcf;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

// TODO write bgzip instead of gzip
public class VcfWriter implements AutoCloseable {
  private final BufferedWriter bufferedWriter;

  public VcfWriter(BufferedWriter bufferedWriter) {
    this.bufferedWriter = requireNonNull(bufferedWriter);
  }

  public void writeHeader(VcfHeader vcfHeader) {
    try {
      for (String line : vcfHeader.lines()) {
        bufferedWriter.write(line);
        bufferedWriter.write('\n');
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void write(VcfRecord vcfRecord) {
    try {
      for (String token : vcfRecord.tokens()) {
        bufferedWriter.write(token);
        bufferedWriter.write('\t');
      }
      bufferedWriter.write('\n');
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static VcfWriter create(OutputStream outputStream) {
    final int bufferedWriterBufferSize = 32768; // TODO benchmark similar to VcfReader
    final int outputStreamWriterBufferSize = 32768;

    BufferedWriter bufferedWriter;
    try {
      bufferedWriter =
          new BufferedWriter(
              new OutputStreamWriter(
                  new GZIPOutputStream(outputStream, outputStreamWriterBufferSize) {
                    {
                      def =
                          new Deflater(
                              Deflater.BEST_SPEED, true); // hack: set protected 'def' field
                    }
                  }),
              bufferedWriterBufferSize);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new VcfWriter(bufferedWriter);
  }

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
  }
}
