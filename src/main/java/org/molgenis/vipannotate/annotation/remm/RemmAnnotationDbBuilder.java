package org.molgenis.vipannotate.annotation.remm;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;

public class RemmAnnotationDbBuilder {
  public RemmAnnotationDbBuilder() {}

  public void create(Path remmFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(remmFile)) {
      Iterator<RemmAnnotatedPosition> iterator = create(reader, fastaIndex);

      MemoryBuffer reusableMemoryBuffer = MemoryBuffer.newHeapBuffer((1 << 20) * Byte.BYTES);

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);

      // annotation encoder
      RemmAnnotationEncoder annotationEncoder = new RemmAnnotationEncoder();

      // annotation dataset encoder
      IndexedAnnotatedFeatureDatasetEncoder<DoubleValueAnnotation> annotationDatasetEncoder =
          new IndexedAnnotatedFeatureDatasetEncoder<>(annotationEncoder, reusableMemoryBuffer);

      // annotation dataset writer
      AnnotatedPositionPartitionWriter<Position, DoubleValueAnnotation, RemmAnnotatedPosition>
          annotationDatasetWriter =
              new AnnotatedPositionPartitionWriter<>(
                  "score", annotationDatasetEncoder, binaryPartitionWriter);

      AnnotatedIntervalDbWriter<Position, DoubleValueAnnotation, RemmAnnotatedPosition>
          annotationDbWriter = new AnnotatedIntervalDbWriter<>(annotationDatasetWriter);

      annotationDbWriter.write(iterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<RemmAnnotatedPosition> create(
      BufferedReader bufferedReader, FastaIndex fastaIndex) {
    RemmParser remmParser = new RemmParser();
    RemmTsvRecordToRemmAnnotatedPositionMapper mapper =
        new RemmTsvRecordToRemmAnnotatedPositionMapper(fastaIndex);
    return new TransformingIterator<>(
        new FilteringIterator<>(
            new TransformingIterator<>(new TsvIterator(bufferedReader), remmParser::parse),
            e -> fastaIndex.containsReferenceSequence(e.chr())),
        mapper::map);
  }
}
