package org.molgenis.vcf.annotate.db.utils;

import com.github.luben.zstd.RecyclingBufferPool;
import com.github.luben.zstd.ZstdInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.fury.Fury;
import org.apache.fury.io.FuryInputStream;
import org.molgenis.vcf.annotate.db.FuryFactory;
import org.molgenis.vcf.annotate.db.model.AnnotationDb;
import org.molgenis.vcf.annotate.db.model.Chromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationDbImpl {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationDbImpl.class);

  private final ZipFile zipFile;
  private final Fury fury;

  private String currentContigId;
  private AnnotationDb currentAnnotationDb;

  public AnnotationDbImpl(File zipFile) throws IOException {
    this.zipFile = ZipFile.builder().setPath(zipFile.toPath()).get();
    this.fury = FuryFactory.createFury();
  }

  public AnnotationDb get(Chromosome chromosome) {
    String contig =
        switch (chromosome) {
          case CHR1 -> "chr1";
          case CHR2 -> "chr2";
          case CHR3 -> "chr3";
          case CHR4 -> "chr4";
          case CHR5 -> "chr5";
          case CHR6 -> "chr6";
          case CHR7 -> "chr7";
          case CHR8 -> "chr8";
          case CHR9 -> "chr9";
          case CHR10 -> "chr10";
          case CHR11 -> "chr11";
          case CHR12 -> "chr12";
          case CHR13 -> "chr13";
          case CHR14 -> "chr14";
          case CHR15 -> "chr15";
          case CHR16 -> "chr16";
          case CHR17 -> "chr17";
          case CHR18 -> "chr18";
          case CHR19 -> "chr19";
          case CHR20 -> "chr20";
          case CHR21 -> "chr21";
          case CHR22 -> "chr22";
          case CHRX -> "chrX";
          case CHRY -> "chrY";
          case CHRM -> "chrM";
        }; // TODO update 'exact' and always use ref contigs?

    if (!contig.equals(currentContigId)) {
      currentContigId = contig;

      long startCurrentTimeMillis = System.currentTimeMillis();

      int partitionId = 0;
      ZipArchiveEntry entry = zipFile.getEntry(contig + "/ref/" + partitionId + ".zst");
      if (entry == null) { // no annotations exist for this partition
        currentAnnotationDb = null;
      } else {
        // perf: zipFile.getInputStream creates a buffered stream, but FuryInputStream is already
        // buffered
        // perf: ZstdCompressorInputStream collects unnecessary InputStreamStatistics, use
        // ZstdInputStream instead
        try (InputStream inputStream =
            new ZstdInputStream(zipFile.getRawInputStream(entry), RecyclingBufferPool.INSTANCE)) {
          currentAnnotationDb = readDatabase(inputStream, entry.getSize());
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
    return currentAnnotationDb;
  }

  private AnnotationDb readDatabase(InputStream inputStream, long size) throws IOException {
    try (FuryInputStream furyInputStream =
        new FuryInputStream(inputStream, Math.toIntExact(size))) {
      return fury.deserializeJavaObject(furyInputStream, AnnotationDb.class);
    }
  }
}
