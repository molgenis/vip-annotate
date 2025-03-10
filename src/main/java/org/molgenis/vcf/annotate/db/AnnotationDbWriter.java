package org.molgenis.vcf.annotate.db;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.Zstd;
import java.io.*;
import java.util.zip.Deflater;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.fury.Fury;
import org.molgenis.vcf.annotate.db.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationDbWriter {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationDbWriter.class);

  private final Fury fury;

  public AnnotationDbWriter() {
    this(FuryFactory.createFury());
  }

  /** package-private constructor for unit testing */
  AnnotationDbWriter(Fury fury) {
    this.fury = requireNonNull(fury);
  }

  public void writeTranscriptDatabase(GenomeAnnotationDb genomeAnnotationDb, File dbFile) {
    Fury fury = FuryFactory.createFury();

    try (FileOutputStream fileOutputStream = new FileOutputStream(dbFile)) {
      fury.serialize(fileOutputStream, genomeAnnotationDb);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void create(
      GenomeAnnotationDb genomeAnnotationDb, ZipArchiveOutputStream zipArchiveOutputStream) {
    zipArchiveOutputStream.setLevel(Deflater.BEST_COMPRESSION); // FIXME can be removed?

    genomeAnnotationDb
        .annotationDbs()
        .forEach(
            (chromosome, annotationDb) -> {
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

              int partitionId = 0;
              write(contig, partitionId, annotationDb, zipArchiveOutputStream);
            });
  }

  private void write(
      String contig,
      int partitionId,
      AnnotationDb annotationDb,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    // serialize and write to zip entry
    String zipArchiveEntryName = contig + "/ref/" + partitionId + ".zst";
    LOGGER.info("creating database partition {}", zipArchiveEntryName);
    ZipArchiveEntry zipEntry = new ZipArchiveEntry(zipArchiveEntryName);
    zipEntry.setMethod(ZipMethod.ZSTD.getCode());

    try {
      try (ByteArrayOutputStream byteArrayOutputStream =
          new ByteArrayOutputStream(8388608)) { // 8 MB
        fury.serializeJavaObject(byteArrayOutputStream, annotationDb);
        byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray();
        zipEntry.setSize(uncompressedByteArray.length);

        // do not use ultra 20-22 levels because https://github.com/facebook/zstd/issues/435
        byte[] compressedByteArray = Zstd.compress(uncompressedByteArray, 19);
        zipArchiveOutputStream.putArchiveEntry(zipEntry);
        zipArchiveOutputStream.write(compressedByteArray);
        zipArchiveOutputStream.closeArchiveEntry();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
