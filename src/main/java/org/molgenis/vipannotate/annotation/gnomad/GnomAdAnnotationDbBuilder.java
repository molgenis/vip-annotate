package org.molgenis.vipannotate.annotation.gnomad;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.AnnotatedSequenceVariant;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.FuryFactory;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;

public class GnomAdAnnotationDbBuilder {
  public GnomAdAnnotationDbBuilder() {}

  public void create(
      Path gnomAdFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(gnomAdFile)) {
      Iterator<AnnotatedSequenceVariant<GnomAdAnnotation>> gnomAdIterator =
          create(reader, fastaIndex);
      GnomAdAnnotationDatasetEncoder gnomAdAnnotationDataSetEncoder =
          new GnomAdAnnotationDatasetEncoder();

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);
      new AnnotatedSequenceVariantDbWriter<>(
              new GnomAdAnnotatedSequenceVariantPartitionWriter(
                  gnomAdAnnotationDataSetEncoder, binaryPartitionWriter),
              new AnnotationIndexWriter(FuryFactory.createFury(), binaryPartitionWriter))
          .write(gnomAdIterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<AnnotatedSequenceVariant<GnomAdAnnotation>> create(
      BufferedReader bufferedReader, FastaIndex fastaIndex) {
    GnomAdParser gnomAdParser = new GnomAdParser(fastaIndex);
    GnomAdAnnotationCreator gnomadAnnotationCreator = new GnomAdAnnotationCreator();
    return new TransformingIterator<>(
        new TransformingIterator<>(new TsvIterator(bufferedReader), gnomAdParser::parse),
        gnomadAnnotationCreator::annotate);
  }
}
