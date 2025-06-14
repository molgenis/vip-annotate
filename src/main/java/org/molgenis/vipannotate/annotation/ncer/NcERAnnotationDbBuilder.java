package org.molgenis.vipannotate.annotation.ncer;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.zip.Zip;
import org.molgenis.vipannotate.zip.ZipCompressionContextOther;

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(Path ncERFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    ZipCompressionContextOther zipCompressionContext = new ZipCompressionContextOther();
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(ncERFile)) {
      new NcERAnnotationDbWriter()
          .create(new NcERIterator(reader), fastaIndex, zipCompressionContext, zipOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
