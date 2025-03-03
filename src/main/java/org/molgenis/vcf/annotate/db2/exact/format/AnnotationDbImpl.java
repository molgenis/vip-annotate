package org.molgenis.vcf.annotate.db2.exact.format;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    this.zipFile = new ZipFile(requireNonNull(zipFile));
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

      ZipEntry entry = zipFile.getEntry(contig + "/" + partitionId + ".vdb");
      try (InputStream inputStream = zipFile.getInputStream(entry)) {
        currentAnnotationDbPartition = readDatabase(inputStream);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      long timeMillis = System.currentTimeMillis() - startCurrentTimeMillis;
      LOGGER.info(
          "loading database took {} ms (chromosome: {} bin: {})", timeMillis, contig, partitionId);
    }

    return currentAnnotationDbPartition.getVariant(variantAltAllele);
  }

  private AnnotationDbPartition readDatabase(InputStream inputStream) throws IOException {
    try (FuryInputStream furyInputStream = new FuryInputStream(inputStream)) {
      return fury.deserializeJavaObject(furyInputStream, AnnotationDbPartition.class);
    }
  }

  @Override
  public void close() throws IOException {
    zipFile.close();
  }
}
