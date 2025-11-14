package org.molgenis.vipannotate.annotation.spliceai;

import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.Region;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfParser;
import org.molgenis.vipannotate.format.vcf.VcfParserFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;
import org.molgenis.vipannotate.util.*;

public class SpliceAiAnnotationDbBuilder {
  public SpliceAiAnnotationDbBuilder() {}

  public void create(
      Input spliceAiInput,
      HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper,
      @Nullable List<Region> regions,
      ContigRegistry contigRegistry,
      BinaryPartitionWriter partitionWriter,
      MemoryBufferFactory memBufferFactory) {

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

      // TODO check if only needs to be created once
      MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexDispatcherWriter =
          SequenceVariantAnnotationIndexDispatcherWriterFactory.create(memBufferFactory)
              .createWriter();

      new AnnotatedSequenceVariantDbWriter<>(
              new SpliceAiAnnotatedSequenceVariantPartitionWriter(
                  spliceAiAnnotationDatasetEncoder, partitionWriter),
              new SequenceVariantAnnotationIndexWriter<>(indexDispatcherWriter, partitionWriter),
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
