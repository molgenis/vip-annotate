package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.v2.AnnotationDatasetWriter;
import org.molgenis.vipannotate.db.v2.GenomePartitionKey;
import org.molgenis.vipannotate.db.v2.ZipZstdCompressionContext;
import org.molgenis.vipannotate.util.SizedIterable;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class GnomAdShortVariantAnnotationDatasetWriter
    implements AnnotationDatasetWriter<GnomAdShortVariantAnnotationData> {
  @NonNull
  private final GnomAdShortVariantAnnotationDataSetEncoder
      gnomAdShortVariantAnnotationDataSetEncoder;

  @NonNull private final ZipZstdCompressionContext zipZstdCompressionContext;

  @Override
  public void write(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    writeSource(genomePartitionKey, variantAnnotations);
    writeAf(genomePartitionKey, variantAnnotations);
    writeFaf95(genomePartitionKey, variantAnnotations);
    writeFaf99(genomePartitionKey, variantAnnotations);
    writeHn(genomePartitionKey, variantAnnotations);
    writeFilters(genomePartitionKey, variantAnnotations);
    writeCov(genomePartitionKey, variantAnnotations);
  }

  private void writeSource(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeSources(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdShortVariantAnnotationData::source),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "src", memoryBuffer);
  }

  private void writeAf(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeAf(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdShortVariantAnnotationData::af),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "af", memoryBuffer);
  }

  private void writeFaf95(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeFaf95(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdShortVariantAnnotationData::faf95),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "faf95", memoryBuffer);
  }

  private void writeFaf99(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeFaf99(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdShortVariantAnnotationData::faf99),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "faf99", memoryBuffer);
  }

  private void writeHn(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeHn(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdShortVariantAnnotationData::hn),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "hn", memoryBuffer);
  }

  private void writeFilters(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeFilters(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdShortVariantAnnotationData::filters),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "filters", memoryBuffer);
  }

  private void writeCov(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdShortVariantAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeCov(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdShortVariantAnnotationData::cov),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "cov", memoryBuffer);
  }

  private void write(
      GenomePartitionKey genomePartitionKey, String basename, MemoryBuffer memoryBuffer) {
    zipZstdCompressionContext.write(genomePartitionKey, basename, memoryBuffer);
  }
}
