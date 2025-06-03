package org.molgenis.vcf.annotate.db.chrpos.phylop;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.db.chrpos.ContigPosAnnotation;
import org.molgenis.vcf.annotate.db.chrpos.ZipCompressionContext;
import org.molgenis.vcf.annotate.util.FastaIndex;
import org.molgenis.vcf.annotate.util.Zip;

public class PhyloPAnnotationDbBuilder {
  public PhyloPAnnotationDbBuilder() {}

  public void create(
      Path phyloPFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    ZipCompressionContext zipCompressionContext = new ZipCompressionContext();
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(phyloPFile)) {
      new PhyloPAnnotationDbWriter()
          .create(
              new PhyloPVariantIterator(reader),
              fastaIndex,
              zipCompressionContext,
              zipOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  // TODO use bigwig instead of bed as input
  private static class PhyloPVariantIterator implements Iterator<ContigPosAnnotation> {
    private final BufferedReader bufferedReader;

    public PhyloPVariantIterator(BufferedReader bufferedReader) {
      this.bufferedReader = requireNonNull(bufferedReader);
    }

    String line = null;

    @Override
    public boolean hasNext() {
      try {
        line = bufferedReader.readLine();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      return line != null;
    }

    @Override
    public ContigPosAnnotation next() {
      String[] tokens = line.split("\t", -1);
      String contig = tokens[0];
      int pos = Integer.parseInt(tokens[1]) + 1;
      if (Integer.parseInt(tokens[2]) != pos) throw new RuntimeException();
      String score = tokens[3];
      return new ContigPosAnnotation(contig, pos, score);
    }
  }
}
