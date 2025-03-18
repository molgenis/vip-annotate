package org.molgenis.vcf.annotate.db.exact.format;

import com.github.luben.zstd.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;
import org.molgenis.vcf.annotate.util.FuryInputStream;

public class AnnotationDbImpl implements AnnotationDb {
  private final VariantAltAlleleEncoder variantAltAlleleEncoder;
  private final Fury fury;
  private final ZipFile zipFile;
  private final byte[] bytes;

  private AnnotationDbPartition currentAnnotationDbPartition;
  private String currentChromosome;
  private int currentBin = -1;

  public AnnotationDbImpl(Path annotationsZip) {
    this.variantAltAlleleEncoder = new VariantAltAlleleEncoder();
    this.fury = FuryFactory.createFury();

    try {
      // TODO benchmark .setOpenOptions(StandardOpenOption.READ, ExtendedOpenOption.DIRECT) on HDD
      this.zipFile = ZipFile.builder().setPath(annotationsZip).get();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long maxZipArchiveEntrySize = 0;
    for (Enumeration<ZipArchiveEntry> e = zipFile.getEntries(); e.hasMoreElements(); ) {
      long zipArchiveEntrySize = e.nextElement().getSize();
      if (zipArchiveEntrySize > maxZipArchiveEntrySize) {
        maxZipArchiveEntrySize = zipArchiveEntrySize;
      }
    }
    this.bytes = new byte[Math.toIntExact(maxZipArchiveEntrySize)];
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
        // perf: zipFile.getInputStream creates a buffered stream, but FuryInputStream is
        // already buffered
        // perf: ZstdCompressorInputStream collects unnecessary InputStreamStatistics, use
        // ZstdInputStream instead
        try (FuryInputStream furyInputStream =
            new FuryInputStream(
                new ZstdInputStreamNoFinalizer(
                    zipFile.getRawInputStream(entry), RecyclingBufferPool.INSTANCE),
                bytes)) {
          currentAnnotationDbPartition =
              fury.deserializeJavaObject(furyInputStream.getBuffer(), AnnotationDbPartition.class);

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

  @Override
  public void close() throws IOException {
    zipFile.close();
  }
}
