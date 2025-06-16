package org.molgenis.vipannotate.annotation.phylop;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;
import org.molgenis.vipannotate.zip.Zip;
import org.molgenis.vipannotate.zip.ZipZstdCompressionContext;

public class PhyloPAnnotationDbBuilder {
  public PhyloPAnnotationDbBuilder() {}

  public void create(
      Path phyloPFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(phyloPFile)) {
      Iterator<ContigPosAnnotation> iterator = create(reader, fastaIndex);

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      GenomePartitionDataWriter genomePartitionDataWriter =
          new ZipZstdGenomePartitionDataWriter(zipZstdCompressionContext);
      NonIndexedAnnotationDbWriter<ContigPosAnnotation, Double> annotationDbWriter =
          new NonIndexedAnnotationDbWriter<>(
              new LocusAnnotationDatasetWriter<>(
                  "score",
                  new PhyloPAnnotationDatasetEncoder(new PhyloPAnnotationDataCodec()),
                  genomePartitionDataWriter));

      annotationDbWriter.create(iterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<ContigPosAnnotation> create(
      BufferedReader bufferedReader, FastaIndex fastaIndex) {
    PhyloPParser phyloPParser = new PhyloPParser();
    PhyloPBedFeatureToContigPosAnnotationMapper mapper =
        new PhyloPBedFeatureToContigPosAnnotationMapper();
    return new TransformingIterator<>(
        new FilteringIterator<>(
            new TransformingIterator<>(new TsvIterator(bufferedReader), phyloPParser::parse),
            e -> fastaIndex.containsReferenceSequence(e.chr())),
        mapper::map);
  }
}
