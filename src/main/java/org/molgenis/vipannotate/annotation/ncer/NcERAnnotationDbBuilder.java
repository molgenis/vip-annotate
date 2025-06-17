package org.molgenis.vipannotate.annotation.ncer;

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

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(Path ncERFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(ncERFile)) {
      Iterator<ContigPosAnnotation> iterator = create(reader, fastaIndex);

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

      annotationDbWriter.create(iterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<ContigPosAnnotation> create(
      BufferedReader bufferedReader, FastaIndex fastaIndex) {
    NcERParser ncERParser = new NcERParser();
    NcERBedFeatureToContigPosAnnotationMapper mapper =
        new NcERBedFeatureToContigPosAnnotationMapper();

    return new FlatteningIterator<>(
        new TransformingIterator<>(
            new FilteringIterator<>(
                new TransformingIterator<>(new TsvIterator(bufferedReader), ncERParser::parse),
                e -> fastaIndex.containsReferenceSequence(e.chr())),
            mapper::map));
  }
}
