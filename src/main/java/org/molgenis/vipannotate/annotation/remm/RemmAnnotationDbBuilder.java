package org.molgenis.vipannotate.annotation.remm;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fory.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.Region;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;

public class RemmAnnotationDbBuilder {
  public RemmAnnotationDbBuilder() {}

  public void create(
      Path remmFile,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(remmFile)) {
      Iterator<RemmAnnotatedPosition> iterator = create(reader, regions, fastaIndex);

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
      BufferedReader bufferedReader, @Nullable List<Region> regions, FastaIndex fastaIndex) {
    RemmParser remmParser = new RemmParser();
    RemmTsvRecordToRemmAnnotatedPositionMapper mapper =
        new RemmTsvRecordToRemmAnnotatedPositionMapper(fastaIndex);
    return new FilteringIterator<>(
        new TransformingIterator<>(
            new FilteringIterator<>(
                new TransformingIterator<>(new TsvIterator(bufferedReader), remmParser::parse),
                e -> fastaIndex.containsReferenceSequence(e.chr())),
            mapper::map),
        annotatedSequenceVariant -> filter(annotatedSequenceVariant, regions));
  }

  private boolean filter(
      @Nullable RemmAnnotatedPosition annotatedPosition, @Nullable List<Region> regions) {
    boolean keep;
    if (annotatedPosition == null) {
      keep = false;
    } else if (regions == null) {
      keep = true;
    } else {
      keep = false;
      for (Region region : regions) {
        Position position = annotatedPosition.getFeature();
        if (region.getContig().equals(position.getContig())) {
          if (region.getStart() != null) {
            if (position.getStart() >= region.getStart()) {
              if (region.getStop() != null) {
                if (position.getStart() <= region.getStop()) {
                  keep = true;
                  break;
                }
              } else {
                keep = true;
                break;
              }
            }
          } else {
            keep = true;
            break;
          }
        }
      }
    }
    return keep;
  }
}
