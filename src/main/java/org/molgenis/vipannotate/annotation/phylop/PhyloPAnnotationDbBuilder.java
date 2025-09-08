package org.molgenis.vipannotate.annotation.phylop;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fory.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.IndexedAnnotationEncoder;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.*;

public class PhyloPAnnotationDbBuilder {
  public PhyloPAnnotationDbBuilder() {}

  public void create(
      Path phyloPFile,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      ZipArchiveOutputStream zipOutputStream) {

    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(phyloPFile)) {
      Iterator<PhyloPAnnotatedPosition> iterator = create(reader, regions, fastaIndex);

      MemoryBuffer reusableMemoryBuffer = MemoryBuffer.newHeapBuffer((1 << 20) * Short.BYTES);

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);

      // annotation encoder
      IndexedAnnotationEncoder<DoubleValueAnnotation> annotationEncoder =
          new IndexedDoubleValueAnnotationToByteEncoder(-20.0d, 10.003d);

      // annotation dataset encoder
      IndexedAnnotatedFeatureDatasetEncoder<DoubleValueAnnotation> annotationDatasetEncoder =
          new IndexedAnnotatedFeatureDatasetEncoder<>(annotationEncoder, reusableMemoryBuffer);

      // annotation dataset writer
      AnnotatedPositionPartitionWriter<Position, DoubleValueAnnotation, PhyloPAnnotatedPosition>
          annotationDatasetWriter =
              new AnnotatedPositionPartitionWriter<>(
                  "score", annotationDatasetEncoder, binaryPartitionWriter);

      AnnotatedIntervalDbWriter<Position, DoubleValueAnnotation, PhyloPAnnotatedPosition>
          annotationDbWriter = new AnnotatedIntervalDbWriter<>(annotationDatasetWriter);

      annotationDbWriter.write(iterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<PhyloPAnnotatedPosition> create(
      BufferedReader bufferedReader, @Nullable List<Region> regions, FastaIndex fastaIndex) {
    PhyloPParser phyloPParser = new PhyloPParser();
    PhyloPBedFeatureToPhyloPAnnotatedPositionMapper mapper =
        new PhyloPBedFeatureToPhyloPAnnotatedPositionMapper(fastaIndex);
    return new FilteringIterator<>(
        new TransformingIterator<>(
            new FilteringIterator<>(
                new TransformingIterator<>(new TsvIterator(bufferedReader), phyloPParser::parse),
                e -> fastaIndex.containsReferenceSequence(e.chr())),
            mapper::map),
        annotatedPosition -> filter(annotatedPosition, regions));
  }

  private boolean filter(
      @Nullable PhyloPAnnotatedPosition annotatedPosition, @Nullable List<Region> regions) {
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
