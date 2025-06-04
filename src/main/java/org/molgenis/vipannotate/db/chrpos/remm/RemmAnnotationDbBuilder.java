package org.molgenis.vipannotate.db.chrpos.remm;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.db.chrpos.ZipCompressionContext;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.Zip;

public class RemmAnnotationDbBuilder {
  public RemmAnnotationDbBuilder() {}

  public void create(
      Path phyloPFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    ZipCompressionContext zipCompressionContext = new ZipCompressionContext();
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(phyloPFile)) {
      new RemmAnnotationDbWriter()
          .create(new RemmIterator(reader), fastaIndex, zipCompressionContext, zipOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
