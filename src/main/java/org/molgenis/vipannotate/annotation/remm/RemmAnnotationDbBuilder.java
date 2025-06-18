package org.molgenis.vipannotate.annotation.remm;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;

public class RemmAnnotationDbBuilder {
  public RemmAnnotationDbBuilder() {}

  public void create(Path ncERFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(ncERFile)) {
      Iterator<ContigPosAnnotation> iterator = create(reader, fastaIndex);

      MemoryBuffer reusableMemoryBuffer = MemoryBuffer.newHeapBuffer((1 << 20) * Byte.BYTES);

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      GenomePartitionDataWriter genomePartitionDataWriter =
          new ZipZstdGenomePartitionDataWriter(zipZstdCompressionContext);
      NonIndexedAnnotationDbWriter<ContigPosAnnotation, Double> annotationDbWriter =
          new NonIndexedAnnotationDbWriter<>(
              new IntervalAnnotationDatasetWriter<>(
                  "score",
                  new ContigPosScoreAnnotationDatasetEncoder(
                      new RemmAnnotationEncoder(), reusableMemoryBuffer),
                  genomePartitionDataWriter,
                  fastaIndex));

      annotationDbWriter.create(iterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<ContigPosAnnotation> create(BufferedReader bufferedReader, FastaIndex fastaIndex)
      throws IOException {
    RemmParser remmParser = new RemmParser();
    RemmTsvRecordToContigPosAnnotationMapper mapper =
        new RemmTsvRecordToContigPosAnnotationMapper();
    return new TransformingIterator<>(
        new FilteringIterator<>(
            new TransformingIterator<>(new TsvIterator(bufferedReader), remmParser::parse),
            e -> fastaIndex.containsReferenceSequence(e.chr())),
        mapper::map);
  }
}
