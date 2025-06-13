package org.molgenis.vipannotate.annotation.gnomad;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetWriter;
import org.molgenis.vipannotate.annotation.GenomePartitionKey;
import org.molgenis.vipannotate.util.SizedIterable;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;
import org.molgenis.vipannotate.zip.ZipZstdCompressionContext;

@RequiredArgsConstructor
public class GnomAdAnnotationDatasetWriter
    implements AnnotationDatasetWriter<GnomAdAnnotationData> {
  @NonNull private final GnomAdAnnotationDataSetEncoder gnomAdAnnotationDataSetEncoder;

  @NonNull private final ZipZstdCompressionContext zipZstdCompressionContext;

  @Override
  public void write(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
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
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeSources(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdAnnotationData::source),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "src", memoryBuffer);
  }

  private void writeAf(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeAf(
            new SizedIterator<>(
                new TransformingIterator<>(variantAnnotations.iterator(), GnomAdAnnotationData::af),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "af", memoryBuffer);
  }

  private void writeFaf95(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeFaf95(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdAnnotationData::faf95),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "faf95", memoryBuffer);
  }

  private void writeFaf99(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeFaf99(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdAnnotationData::faf99),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "faf99", memoryBuffer);
  }

  private void writeHn(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeHn(
            new SizedIterator<>(
                new TransformingIterator<>(variantAnnotations.iterator(), GnomAdAnnotationData::hn),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "hn", memoryBuffer);
  }

  private void writeFilters(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeFilters(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdAnnotationData::filters),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "filters", memoryBuffer);
  }

  private void writeCov(
      GenomePartitionKey genomePartitionKey,
      SizedIterable<GnomAdAnnotationData> variantAnnotations) {
    MemoryBuffer memoryBuffer =
        gnomAdAnnotationDataSetEncoder.encodeCov(
            new SizedIterator<>(
                new TransformingIterator<>(
                    variantAnnotations.iterator(), GnomAdAnnotationData::cov),
                variantAnnotations.getSize()));
    write(genomePartitionKey, "cov", memoryBuffer);
  }

  private void write(
      GenomePartitionKey genomePartitionKey, String basename, MemoryBuffer memoryBuffer) {
    zipZstdCompressionContext.write(genomePartitionKey, basename, memoryBuffer);
  }
}
