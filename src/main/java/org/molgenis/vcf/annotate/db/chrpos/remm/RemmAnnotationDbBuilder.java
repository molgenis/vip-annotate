package org.molgenis.vcf.annotate.db.chrpos.remm;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.db.chrpos.ZipCompressionContext;
import org.molgenis.vcf.annotate.util.FastaIndex;
import org.molgenis.vcf.annotate.util.Zip;

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
