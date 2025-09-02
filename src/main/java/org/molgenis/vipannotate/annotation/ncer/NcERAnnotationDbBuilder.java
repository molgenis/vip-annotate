package org.molgenis.vipannotate.annotation.ncer;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.*;

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(
      Path ncERFile,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(ncERFile)) {
      Iterator<NcERAnnotatedPosition> iterator = create(reader, regions, fastaIndex);

      MemoryBuffer reusableMemoryBuffer =
          MemoryBuffer.newHeapBuffer((1 << 20) * Short.BYTES); // FIXME 18, other places as well?

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);

      // annotation encoder
      NcERAnnotationEncoder annotationEncoder = new NcERAnnotationEncoder();

      // annotation dataset encoder
      IndexedAnnotatedFeatureDatasetEncoder<DoubleValueAnnotation> annotationDatasetEncoder =
          new IndexedAnnotatedFeatureDatasetEncoder<>(annotationEncoder, reusableMemoryBuffer);

      // annotation dataset writer
      AnnotatedPositionPartitionWriter<Position, DoubleValueAnnotation, NcERAnnotatedPosition>
          annotationDatasetWriter =
              new AnnotatedPositionPartitionWriter<>(
                  "score", annotationDatasetEncoder, binaryPartitionWriter);

      AnnotatedIntervalDbWriter<Position, DoubleValueAnnotation, NcERAnnotatedPosition>
          annotationDbWriter = new AnnotatedIntervalDbWriter<>(annotationDatasetWriter);

      annotationDbWriter.write(iterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<NcERAnnotatedPosition> create(
      BufferedReader bufferedReader, @Nullable List<Region> regions, FastaIndex fastaIndex) {
    NcERParser ncERParser = new NcERParser();
    NcERBedFeatureToNcERAnnotatedPositionMapper mapper =
        new NcERBedFeatureToNcERAnnotatedPositionMapper(fastaIndex);

    return new FilteringIterator<>(
        new FlatteningIterator<>(
            new TransformingIterator<>(
                new FilteringIterator<>(
                    new TransformingIterator<>(new TsvIterator(bufferedReader), ncERParser::parse),
                    e -> fastaIndex.containsReferenceSequence(e.chr())),
                mapper::map)),
        annotatedSequenceVariant -> filter(annotatedSequenceVariant, regions));
  }

  private boolean filter(
      @Nullable NcERAnnotatedPosition annotatedPosition, @Nullable List<Region> regions) {
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
