package org.molgenis.vipannotate.db.chrpos.ncer;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.db.chrpos.ZipCompressionContext;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.Zip;

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(
      Path phyloPFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    ZipCompressionContext zipCompressionContext = new ZipCompressionContext();
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(phyloPFile)) {
      new NcERAnnotationDbWriter()
          .create(new NcERIterator(reader), fastaIndex, zipCompressionContext, zipOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
