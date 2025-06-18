package org.molgenis.vipannotate.annotation.ncer;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationDatasetEncoder;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.*;

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(Path ncERFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(ncERFile)) {
      Iterator<ContigPosAnnotation> iterator = create(reader, fastaIndex);

      MemoryBuffer reusableMemoryBuffer = MemoryBuffer.newHeapBuffer((1 << 20) * Short.BYTES);

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      GenomePartitionDataWriter genomePartitionDataWriter =
          new ZipZstdGenomePartitionDataWriter(zipZstdCompressionContext);
      NonIndexedAnnotationDbWriter<ContigPosAnnotation, Double> annotationDbWriter =
          new NonIndexedAnnotationDbWriter<>(
              new IntervalAnnotationDatasetWriter<>(
                  "score",
                  new ContigPosScoreAnnotationDatasetEncoder(
                      new NcERAnnotationEncoder(), reusableMemoryBuffer),
                  genomePartitionDataWriter,
                  fastaIndex));

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
