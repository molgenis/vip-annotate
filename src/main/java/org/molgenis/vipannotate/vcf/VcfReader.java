package org.molgenis.vipannotate.vcf;

import java.io.*;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VcfReader implements Iterator<VcfRecord>, AutoCloseable {
  @Getter private final VcfHeader header;
  private final VcfRecordIterator recordIterator;

  @Override
  public boolean hasNext() {
    return this.recordIterator.hasNext();
  }

  @Override
  public VcfRecord next() {
    return this.recordIterator.next();
  }

  @Override
  public void close() throws Exception {
    this.recordIterator.close();
  }

  public static VcfReader create(InputStream inputStream) {
    final int bufferedReaderBufferSize = 32768; // see BgzipDecompressBenchmark
    final int inputStreamReaderBufferSize = 32768;

    BufferedReader bufferedReader;
    try {
      bufferedReader =
          new BufferedReader(
              new InputStreamReader(new GZIPInputStream(inputStream, inputStreamReaderBufferSize)),
              bufferedReaderBufferSize);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    VcfHeader vcfHeader = VcfHeader.create(bufferedReader);
    VcfRecordIterator vcfRecordIterator = new VcfRecordIterator(bufferedReader);
    return new VcfReader(vcfHeader, vcfRecordIterator);
  }
}
