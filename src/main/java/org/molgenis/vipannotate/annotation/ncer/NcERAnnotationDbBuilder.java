package org.molgenis.vipannotate.annotation.ncer;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.util.*;
import org.molgenis.vipannotate.util.Gzip;

public class NcERAnnotationDbBuilder {
  public NcERAnnotationDbBuilder() {}

  public void create(
      Input ncERInput,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      BinaryPartitionWriter partitionWriter) {
    try (BufferedReader reader = Gzip.createBufferedReaderUtf8FromGzip(ncERInput)) {
      Iterator<NcERAnnotatedPosition> iterator = create(reader, regions, fastaIndex);

      // annotation encoder
      NcERAnnotationEncoder annotationEncoder = new NcERAnnotationEncoder(new DoubleCodec());

      // annotation dataset writer
      try (AnnotatedPositionPartitionWriter<Position, DoubleValueAnnotation, NcERAnnotatedPosition>
          posPartitionWriter =
              new AnnotatedPositionPartitionWriter<>(
                  "score",
                  new IndexedAnnotatedFeatureDatasetEncoder<>(annotationEncoder),
                  partitionWriter)) {

        AnnotatedIntervalDbWriter<Position, DoubleValueAnnotation, NcERAnnotatedPosition>
            annotationDbWriter = new AnnotatedIntervalDbWriter<>(posPartitionWriter);

        annotationDbWriter.write(iterator);
      }
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
