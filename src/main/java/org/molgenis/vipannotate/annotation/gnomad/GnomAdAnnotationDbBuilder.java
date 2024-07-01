package org.molgenis.vipannotate.annotation.gnomad;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.AnnotatedSequenceVariant;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.zip.Zip;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.util.FilteringIterator;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.util.TsvIterator;
import org.molgenis.zstd.ZstdProvider;

public class GnomAdAnnotationDbBuilder {
  public GnomAdAnnotationDbBuilder() {}

  public void create(
      Input gnomAdInput,
      @Nullable List<Region> regions,
      FastaIndex fastaIndex,
      ZipArchiveOutputStream zipOutputStream) {
    try (BufferedReader reader = Zip.createBufferedReaderUtf8FromGzip(gnomAdInput)) {
      Iterator<AnnotatedSequenceVariant<GnomAdAnnotation>> gnomAdIterator =
          create(reader, regions, fastaIndex);
      GnomAdAnnotationDatasetEncoder gnomAdAnnotationDataSetEncoder =
          new GnomAdAnnotationDatasetEncoder();

      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(
              zipOutputStream, ZstdProvider.INSTANCE.get().createCompressionContext());
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);
      // TODO check if only needs to be created once
      BinarySerializer<AnnotationIndex<SequenceVariant>> indexDispatcherSerializer =
          SequenceVariantAnnotationIndexDispatcherSerializerFactory.create().createSerializer();
      new AnnotatedSequenceVariantDbWriter<>(
              new GnomAdAnnotatedSequenceVariantPartitionWriter(
                  gnomAdAnnotationDataSetEncoder, binaryPartitionWriter),
              new SequenceVariantAnnotationIndexWriter<>(
                  indexDispatcherSerializer, binaryPartitionWriter),
              SequenceVariantEncoderDispatcherFactory.create())
          .write(gnomAdIterator);
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
