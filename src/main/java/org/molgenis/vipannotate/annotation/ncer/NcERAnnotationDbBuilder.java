package org.molgenis.vipannotate.annotation.ncer;

import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.zip.Zip;
import org.molgenis.vipannotate.zip.ZipZstdCompressionContext;

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(Path ncERFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    // FIXME
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(ncERFile)) {
      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      GenomePartitionDataWriter genomePartitionDataWriter =
          new ZipZstdGenomePartitionDataWriter(zipZstdCompressionContext);
      NonIndexedAnnotationDbWriter<ContigPosAnnotation, Double> annotationDbWriter =
          new NonIndexedAnnotationDbWriter<>(
              new LocusAnnotationDatasetWriter<>(
                  "score",
                  new NcERAnnotationDatasetEncoder(new NcERAnnotationDataCodec()),
                  genomePartitionDataWriter));
      annotationDbWriter.create(new NcERIterator(reader));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
