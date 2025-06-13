package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.annotation.AnnotationDbWriter;
import org.molgenis.vipannotate.annotation.AnnotationIndexWriter;
import org.molgenis.vipannotate.annotation.VariantAnnotation;
import org.molgenis.vipannotate.serialization.FuryFactory;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;
import org.molgenis.vipannotate.zip.Zip;
import org.molgenis.vipannotate.zip.ZipZstdCompressionContext;

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
