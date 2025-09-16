package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfParser;
import org.molgenis.vipannotate.format.vcf.VcfParserFactory;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.ForyFactory;
import org.molgenis.vipannotate.util.*;

public class SpliceAiAnnotationDbBuilder {
  public SpliceAiAnnotationDbBuilder() {}

  public void create(
      Path spliceAiFile,
      HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper,
      @Nullable List<Region> regions,
      ContigRegistry contigRegistry,
      ZipArchiveOutputStream zipOutputStream) {

    SpliceAiParser spliceAiParser = new SpliceAiParser();

    SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper mapper =
        new SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper(
            contigRegistry, hgncToNcbiGeneIdMapper);
    try (VcfParser vcfParser = VcfParserFactory.create(spliceAiFile)) {
      Iterator<AnnotatedSequenceVariant<SpliceAiAnnotation>> spliceAiIt =
          new FilteringIterator<>(
              new TransformingIterator<>(
                  new TransformingIterator<>(vcfParser, spliceAiParser::parse), mapper::annotate),
              annotatedSequenceVariant -> filter(annotatedSequenceVariant, regions));

      SpliceAiAnnotationDatasetEncoder spliceAiAnnotationDatasetEncoder =
          new SpliceAiAnnotationDatasetEncoder();
      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);
      new AnnotatedSequenceVariantDbWriter<>(
              new SpliceAiAnnotatedSequenceVariantPartitionWriter(
                  spliceAiAnnotationDatasetEncoder, binaryPartitionWriter),
              new AnnotationIndexWriter(ForyFactory.createFory(), binaryPartitionWriter))
          .write(spliceAiIt);
    }
  }

  private int compare(VcfRecord vcfRecord1, VcfRecord vcfRecord2) {
    if (!vcfRecord1.chrom().equals(vcfRecord2.chrom())) {
      return -1;
    }
    return vcfRecord1.pos() <= vcfRecord2.pos() ? -1 : 1;
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
        if (region.getContig().equals(sequenceVariant.getContig())) {
          if (region.getStart() != null) {
            if (sequenceVariant.getStart() >= region.getStart()) {
              if (region.getStop() != null) {
                if (sequenceVariant.getStart() <= region.getStop()) {
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
