package org.molgenis.vipannotate.annotation.gnomad;

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

public class GnomAdAnnotationDbBuilder {
  public GnomAdAnnotationDbBuilder() {}

  public void create(
      Path gnomAdFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(gnomAdFile)) {
      Iterator<VariantAnnotation<GnomAdAnnotationData>> gnomAdIterator = create(reader, fastaIndex);
      GnomAdAnnotationDatasetEncoder gnomAdAnnotationDataSetEncoder =
          new GnomAdAnnotationDatasetEncoder();
      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      new AnnotationDbWriter<>(
              new AnnotationIndexWriter(FuryFactory.createFury(), zipZstdCompressionContext),
              new GnomAdAnnotationDatasetWriter(
                  gnomAdAnnotationDataSetEncoder, zipZstdCompressionContext))
          .create(gnomAdIterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<VariantAnnotation<GnomAdAnnotationData>> create(
      BufferedReader bufferedReader, FastaIndex fastaIndex) {
    GnomAdParser gnomAdParser = new GnomAdParser(fastaIndex);
    GnomAdAnnotationCreator gnomadAnnotationCreator = new GnomAdAnnotationCreator();
    return new TransformingIterator<>(
        new TransformingIterator<>(new TsvIterator(bufferedReader), gnomAdParser::parse),
        gnomadAnnotationCreator::annotate);
  }
}
