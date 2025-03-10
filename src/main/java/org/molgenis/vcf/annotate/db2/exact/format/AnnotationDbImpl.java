package org.molgenis.vcf.annotate.db2.exact.format;

import com.github.luben.zstd.RecyclingBufferPool;
import com.github.luben.zstd.ZstdInputStream;
import java.io.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.fury.Fury;
import org.apache.fury.io.FuryInputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db2.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db2.exact.VariantAltAlleleEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationDbImpl implements AnnotationDb {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationDbImpl.class);

  private final ZipFile zipFile;
  private final VariantAltAlleleEncoder variantAltAlleleEncoder;
  private final Fury fury;

  private AnnotationDbPartition currentAnnotationDbPartition;
  private String currentChromosome;
  private int currentBin = -1;

  public AnnotationDbImpl(File zipFile) throws IOException {
    this.zipFile = ZipFile.builder().setPath(zipFile.toPath()).get();
    this.variantAltAlleleEncoder = new VariantAltAlleleEncoder();
    this.fury = FuryFactory.createFury();
  }

  @Override
  public MemoryBuffer findVariant(String contig, int start, int stop, byte[] altBases) {
    VariantAltAllele variantAltAllele = new VariantAltAllele(contig, start, stop, altBases);

    int partitionId = variantAltAlleleEncoder.getPartitionId(variantAltAllele);
    if (partitionId != currentBin || !contig.equals(currentChromosome)) {
      currentChromosome = contig;
      currentBin = partitionId;
      long startCurrentTimeMillis = System.currentTimeMillis();

      ZipArchiveEntry entry = zipFile.getEntry(contig + "/" + partitionId + ".zst");
      if (entry == null) { // no annotations exist for this partition
        currentAnnotationDbPartition = null;
      } else {
        // perf: zipFile.getInputStream creates a buffered stream, but FuryInputStream is already
        // buffered
        // perf: ZstdCompressorInputStream collects unnecessary InputStreamStatistics, use
        // ZstdInputStream instead
        try (InputStream inputStream =
            new ZstdInputStream(zipFile.getRawInputStream(entry), RecyclingBufferPool.INSTANCE)) {
          currentAnnotationDbPartition = readDatabase(inputStream, entry.getSize());
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
        long timeMillis = System.currentTimeMillis() - startCurrentTimeMillis;
        LOGGER.debug(
            "loading database took {} ms (chromosome: {} bin: {})",
            timeMillis,
            contig,
            partitionId);
      }
    }

    return currentAnnotationDbPartition != null
        ? currentAnnotationDbPartition.getVariant(variantAltAllele)
        : null;
  }

  private AnnotationDbPartition readDatabase(InputStream inputStream, long size)
      throws IOException {
    try (FuryInputStream furyInputStream =
        new FuryInputStream(inputStream, Math.toIntExact(size))) {
      return fury.deserializeJavaObject(furyInputStream, AnnotationDbPartition.class);
    }
  }

  @Override
  public void close() throws IOException {
    zipFile.close();
  }
}
