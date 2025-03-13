package org.molgenis.vcf.annotate.db.exact.format;

import com.github.luben.zstd.RecyclingBufferPool;
import com.github.luben.zstd.ZstdInputStream;
import java.io.*;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.fury.Fury;
import org.apache.fury.io.FuryInputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;

public class AnnotationDbImpl implements AnnotationDb {
  private final ZipFile zipFile;
  private final VariantAltAlleleEncoder variantAltAlleleEncoder;
  private final Fury fury;

  private AnnotationDbPartition currentAnnotationDbPartition;
  private String currentChromosome;
  private int currentBin = -1;

  public AnnotationDbImpl(Path annotationsZip) {
    try {
      this.zipFile = ZipFile.builder().setPath(annotationsZip).get();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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

      ZipArchiveEntry entry = zipFile.getEntry(contig + "/var/" + partitionId + ".zst");
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
      }
    }

    // FIXME support alternate alleles with 'N'
    for (byte altBase : altBases) {
      if (altBase == 'N') {
        return null;
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
