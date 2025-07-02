package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.vcf.VcfParser;
import org.molgenis.vipannotate.format.vcf.VcfParserFactory;
import org.molgenis.vipannotate.format.zip.ZipZstdCompressionContext;
import org.molgenis.vipannotate.serialization.FuryFactory;
import org.molgenis.vipannotate.util.TransformingIterator;

public class SpliceAiAnnotationDbBuilder {
  public SpliceAiAnnotationDbBuilder() {}

  public void create(
      Path spliceAiFile, FastaIndex fastaIndex, ZipArchiveOutputStream zipOutputStream) {

    SpliceAiParser spliceAiParser = new SpliceAiParser();
    ContigRegistry contigRegistry = ContigRegistry.create(fastaIndex);
    SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper mapper =
        new SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper(contigRegistry);
    try (VcfParser vcfParser = VcfParserFactory.create(spliceAiFile)) {
      TransformingIterator<
              SpliceAiVcfRecord, @Nullable AnnotatedSequenceVariant<SpliceAiAnnotation>>
          spliceAiIt =
              new TransformingIterator<>(
                  new TransformingIterator<>(vcfParser, spliceAiParser::parse),
                  mapper::annotate);

      SpliceAiAnnotationDatasetEncoder spliceAiAnnotationDatasetEncoder =
          new SpliceAiAnnotationDatasetEncoder();
      ZipZstdCompressionContext zipZstdCompressionContext =
          new ZipZstdCompressionContext(zipOutputStream);
      BinaryPartitionWriter binaryPartitionWriter =
          new ZipZstdBinaryPartitionWriter(zipZstdCompressionContext);
      new AnnotatedSequenceVariantDbWriter<>(
              new SpliceAiAnnotatedSequenceVariantPartitionWriter(
                  spliceAiAnnotationDatasetEncoder, binaryPartitionWriter),
              new AnnotationIndexWriter(FuryFactory.createFury(), binaryPartitionWriter))
          .write(spliceAiIt);
    }
  }
}
