package org.molgenis.vipannotate.db.gnomad.shortvariant.db;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.db.exact.format.FuryFactory;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationData;
import org.molgenis.vipannotate.db.v2.AnnotationDbWriter;
import org.molgenis.vipannotate.db.v2.AnnotationIndexWriter;
import org.molgenis.vipannotate.db.v2.VariantAnnotation;
import org.molgenis.vipannotate.db.v2.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;
import org.molgenis.vipannotate.util.Zip;

public class GnomAdShortVariantAnnotationDbBuilder {
  public GnomAdShortVariantAnnotationDbBuilder() {}

  public void create(
      Path gnomAdFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(gnomAdFile)) {
      Iterator<VariantAnnotation<GnomAdShortVariantAnnotationData>> gnomAdShortVariantIterator =
          create(reader, fastaIndex);
      GnomAdShortVariantAnnotationDataSetEncoder gnomAdShortVariantAnnotationDataSetEncoder =
          new GnomAdShortVariantAnnotationDataSetEncoder();
      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      new AnnotationDbWriter<>(
              new AnnotationIndexWriter(FuryFactory.createFury(), zipZstdCompressionContext),
              new GnomAdShortVariantAnnotationDatasetWriter(
                  gnomAdShortVariantAnnotationDataSetEncoder, zipZstdCompressionContext))
          .create(gnomAdShortVariantIterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<VariantAnnotation<GnomAdShortVariantAnnotationData>> create(
      BufferedReader bufferedReader, FastaIndex fastaIndex) throws IOException {
    GnomAdShortVariantParser gnomAdShortVariantParser = new GnomAdShortVariantParser(fastaIndex);
    GnomAdShortVariantAnnotationCreator gnomadShortVariantAnnotationCreator =
        new GnomAdShortVariantAnnotationCreator();
    return new TransformingIterator<>(
        new TransformingIterator<>(
            new TsvIterator(bufferedReader), gnomAdShortVariantParser::parse),
        gnomadShortVariantAnnotationCreator::annotate);
  }
}
