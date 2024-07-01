package org.molgenis.vipannotate.annotation.spliceai;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfParser;
import org.molgenis.vipannotate.format.vcf.VcfParserFactory;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.util.*;
import org.molgenis.zstd.ZstdProvider;

public class SpliceAiAnnotationDbBuilder {
  public SpliceAiAnnotationDbBuilder() {}

  public void create(
      Input spliceAiInput,
      HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper,
      @Nullable List<Region> regions,
      ContigRegistry contigRegistry,
      ZipArchiveOutputStream zipOutputStream) {

    SpliceAiParser spliceAiParser = new SpliceAiParser();

    SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper mapper =
        new SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper(
            contigRegistry, hgncToNcbiGeneIdMapper);
    try (VcfParser vcfParser = VcfParserFactory.create(spliceAiInput)) {
      Iterator<AnnotatedSequenceVariant<SpliceAiAnnotation>> spliceAiIt =
          new FilteringIterator<>(
              new TransformingIterator<>(
                  new TransformingIterator<>(
                      new FlatteningIterator<>(vcfParser), spliceAiParser::parse),
                  mapper::annotate),
              annotatedSequenceVariant -> filter(annotatedSequenceVariant, regions));

      SpliceAiAnnotationDatasetEncoder spliceAiAnnotationDatasetEncoder =
          new SpliceAiAnnotationDatasetEncoder();
      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(
              zipOutputStream, ZstdProvider.INSTANCE.get().createCompressionContext());
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);
      // TODO check if only needs to be created once
      BinarySerializer<AnnotationIndex<SequenceVariant>> indexDispatcherSerializer =
          SequenceVariantAnnotationIndexDispatcherSerializerFactory.create().createSerializer();
      new AnnotatedSequenceVariantDbWriter<>(
              new SpliceAiAnnotatedSequenceVariantPartitionWriter(
                  spliceAiAnnotationDatasetEncoder, binaryPartitionWriter),
              new SequenceVariantAnnotationIndexWriter<>(
                  indexDispatcherSerializer, binaryPartitionWriter),
              SequenceVariantEncoderDispatcherFactory.create())
          .write(spliceAiIt);
    }
  }

  private boolean filter(
      @Nullable AnnotatedSequenceVariant<SpliceAiAnnotation>
          spliceAiAnnotationAnnotatedSequenceVariant,
      @Nullable List<Region> regions) {
    boolean keep;
    if (spliceAiAnnotationAnnotatedSequenceVariant == null) {
      keep = false;
    } else if (regions == null) {
      keep = true;
    } else {
      keep = false;
      for (Region region : regions) {
        SequenceVariant sequenceVariant = spliceAiAnnotationAnnotatedSequenceVariant.getFeature();
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
