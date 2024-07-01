package org.molgenis.vipannotate.annotation.remm;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.*;
import org.molgenis.zstd.ZstdProvider;

public class RemmAnnotationDbBuilder {
  public RemmAnnotationDbBuilder() {}

  public void create(
      Input remmInput,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(remmInput)) {
      Iterator<RemmAnnotatedPosition> iterator = create(reader, regions, fastaIndex);

      MemoryBuffer reusableMemoryBuffer = MemoryBuffer.wrap(new byte[1 << 18]);

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(
              zipOutputStream, ZstdProvider.INSTANCE.get().createCompressionContext());
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);

      // annotation encoder
      RemmAnnotationEncoder annotationEncoder = new RemmAnnotationEncoder(new DoubleCodec());

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
        if (region.contig().equals(position.getContig())) {
          if (region.start() != null) {
            if (position.getStart() >= region.start()) {
              if (region.stop() != null) {
                if (position.getStart() <= region.stop()) {
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
