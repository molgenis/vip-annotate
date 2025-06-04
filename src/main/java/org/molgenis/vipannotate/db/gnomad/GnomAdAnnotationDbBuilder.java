package org.molgenis.vipannotate.db.gnomad;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.db.exact.AnnotationDbWriter;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.Zip;

public class GnomAdAnnotationDbBuilder {
  public GnomAdAnnotationDbBuilder() {}

  public void create(
      Path gnomAdFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(gnomAdFile)) {
      ZipCompressionContextCreator zipCompressionContextCreator =
          new ZipCompressionContextCreator();
      ZipCompressionContext zipCompressionContext =
          zipCompressionContextCreator.create(new GnomAdShortVariantIterator(reader));
      try (BufferedReader reader2 = Zip.createBufferedReaderUtf8FromGzip(gnomAdFile)) {
        new AnnotationDbWriter()
            .create(
                new GnomAdShortVariantIterator(reader2), zipCompressionContext, zipOutputStream);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
