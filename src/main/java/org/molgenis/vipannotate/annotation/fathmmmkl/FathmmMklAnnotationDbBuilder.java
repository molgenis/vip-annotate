package org.molgenis.vipannotate.annotation.fathmmmkl;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.AnnotatedSequenceVariant;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.ForyFactory;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.Region;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;

public class FathmmMklAnnotationDbBuilder {
  public FathmmMklAnnotationDbBuilder() {}

  public void create(
      Path inputFile,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(inputFile)) {
      Iterator<AnnotatedSequenceVariant<FathmmMklAnnotation>> sequenceVariantIterator =
          create(reader, regions, fastaIndex);
      FathmmMklAnnotationDatasetEncoder annotationDatasetEncoder =
          new FathmmMklAnnotationDatasetEncoder();

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);
      new AnnotatedSequenceVariantDbWriter<>(
              new FathmmMklAnnotatedSequenceVariantPartitionWriter(
                  annotationDatasetEncoder, binaryPartitionWriter),
              new AnnotationIndexWriter(ForyFactory.createFory(), binaryPartitionWriter))
          .write(sequenceVariantIterator);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Iterator<AnnotatedSequenceVariant<FathmmMklAnnotation>> create(
      BufferedReader bufferedReader, @Nullable List<Region> regions, FastaIndex fastaIndex) {
    FathmmMklParser fathmmMklParser = new FathmmMklParser(fastaIndex);
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
