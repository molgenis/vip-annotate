package org.molgenis.vipannotate.annotation.remm;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.zip.Zip;
import org.molgenis.vipannotate.zip.ZipCompressionContextOther;

public class RemmAnnotationDbBuilder {
  public RemmAnnotationDbBuilder() {}

  public void create(
      Path phyloPFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    ZipCompressionContextOther zipCompressionContext = new ZipCompressionContextOther();
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(phyloPFile)) {
      new RemmAnnotationDbWriter()
          .create(new RemmIterator(reader), fastaIndex, zipCompressionContext, zipOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
