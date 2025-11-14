package org.molgenis.vipannotate.annotation.fathmmmkl;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.AnnotatedSequenceVariant;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.Gzip;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;

public class FathmmMklAnnotationDbBuilder {
  public FathmmMklAnnotationDbBuilder() {}

  public void create(
      Input input,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      BinaryPartitionWriter partitionWriter,
      MemoryBufferFactory memBufferFactory) {
    try (BufferedReader reader = Gzip.createBufferedReaderUtf8FromGzip(input)) {
      Iterator<AnnotatedSequenceVariant<FathmmMklAnnotation>> sequenceVariantIterator =
          create(reader, regions, fastaIndex);
      FathmmMklAnnotationDatasetEncoder annotationDatasetEncoder =
          new FathmmMklAnnotationDatasetEncoder();

      // TODO check if only needs to be created once
      MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexDispatcherWriter =
          SequenceVariantAnnotationIndexDispatcherWriterFactory.create(memBufferFactory)
              .createWriter();

      new AnnotatedSequenceVariantDbWriter<>(
              new FathmmMklAnnotatedSequenceVariantPartitionWriter(
                  annotationDatasetEncoder, partitionWriter),
              new SequenceVariantAnnotationIndexWriter<>(indexDispatcherWriter, partitionWriter),
              SequenceVariantEncoderDispatcherFactory.create())
          .write(sequenceVariantIterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<AnnotatedSequenceVariant<FathmmMklAnnotation>> create(
      BufferedReader bufferedReader, @Nullable List<Region> regions, FastaIndex fastaIndex) {
    FathmmMklParser fathmmMklParser = new FathmmMklParser();
    FathmmMklTsvRecordToFathmmMklAnnotatedSequenceVariantMapper tsvRecordToSequenceVariantMapper =
        new FathmmMklTsvRecordToFathmmMklAnnotatedSequenceVariantMapper(fastaIndex);
    // input file can contain records with -99 score, remove these records
    return new FilteringIterator<>(
        new TransformingIterator<>(
            new FilteringIterator<>(
                new TransformingIterator<>(new TsvIterator(bufferedReader), fathmmMklParser::parse),
                x ->
                    fastaIndex.containsReferenceSequence(x.chrom())
                        && !(x.score() < 0 || x.score() > 1)),
            tsvRecordToSequenceVariantMapper::annotate),
        annotatedSequenceVariant -> filter(annotatedSequenceVariant, regions));
  }

  private boolean filter(
      @Nullable AnnotatedSequenceVariant<FathmmMklAnnotation> annotatedSequenceVariant,
      @Nullable List<Region> regions) {
    boolean keep;
    if (annotatedSequenceVariant == null) {
      keep = false;
    } else if (regions == null) {
      keep = true;
    } else {
      keep = false;
      for (Region region : regions) {
        SequenceVariant sequenceVariant = annotatedSequenceVariant.getFeature();
        if (region.contig().equals(sequenceVariant.getContig())) {
          if (region.start() != null) {
            if (sequenceVariant.getStart() >= region.start()) {
              if (region.stop() != null) {
                if (sequenceVariant.getStart() <= region.stop()) {
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
