package org.molgenis.vipannotate.annotation.gnomad;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.AnnotatedSequenceVariant;
import org.molgenis.vipannotate.annotation.Region;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.Gzip;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;

public class GnomAdAnnotationDbBuilder {
  public GnomAdAnnotationDbBuilder() {}

  public void create(
      Input gnomAdInput,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      BinaryPartitionWriter partitionWriter,
      MemoryBufferFactory memBufferFactory) {
    try (BufferedReader reader = Gzip.createBufferedReaderUtf8FromGzip(gnomAdInput)) {
      Iterator<AnnotatedSequenceVariant<GnomAdAnnotation>> gnomAdIterator =
          create(reader, regions, fastaIndex);
      GnomAdAnnotationDatasetEncoder gnomAdAnnotationDataSetEncoder =
          new GnomAdAnnotationDatasetEncoder();

      // TODO check if only needs to be created once
      MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexDispatcherWriter =
          SequenceVariantAnnotationIndexDispatcherWriterFactory.create(memBufferFactory)
              .createWriter();

      try (GnomAdAnnotatedSequenceVariantPartitionWriter gnomAdPartitionWriter =
          new GnomAdAnnotatedSequenceVariantPartitionWriter(
              gnomAdAnnotationDataSetEncoder, partitionWriter)) {
        new AnnotatedSequenceVariantDbWriter<>(
                gnomAdPartitionWriter,
                new SequenceVariantAnnotationIndexWriter<>(indexDispatcherWriter, partitionWriter),
                SequenceVariantEncoderDispatcherFactory.create())
            .write(gnomAdIterator);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<AnnotatedSequenceVariant<GnomAdAnnotation>> create(
      BufferedReader bufferedReader, @Nullable List<Region> regions, FastaIndex fastaIndex) {
    GnomAdParser gnomAdParser = new GnomAdParser(fastaIndex);
    GnomAdTsvRecordToGnomAdAnnotatedSequenceVariantMapper gnomadAnnotationCreator =
        new GnomAdTsvRecordToGnomAdAnnotatedSequenceVariantMapper(fastaIndex);
    return new FilteringIterator<>(
        new TransformingIterator<>(
            new TransformingIterator<>(new TsvIterator(bufferedReader), gnomAdParser::parse),
            gnomadAnnotationCreator::annotate),
        annotatedSequenceVariant -> filter(annotatedSequenceVariant, regions));
  }

  private boolean filter(
      @Nullable AnnotatedSequenceVariant<GnomAdAnnotation> annotatedSequenceVariant,
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
