package org.molgenis.vcf.annotate.db.effect;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.Zstd;
import java.io.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.fury.Fury;
import org.molgenis.vcf.annotate.db.effect.model.*;
import org.molgenis.vcf.annotate.util.Logger;

public class AnnotationDbWriter {
  private final Fury fury;

  public AnnotationDbWriter() {
    this(FuryFactory.createFury());
  }

  /** package-private constructor for unit testing */
  AnnotationDbWriter(Fury fury) {
    this.fury = requireNonNull(fury);
  }

  public void create(
      GenomeAnnotationDb genomeAnnotationDb, ZipArchiveOutputStream zipArchiveOutputStream) {
    genomeAnnotationDb
        .annotationDbs()
        .forEach(
            (chromosome, annotationDb) -> {
              int partitionId = 0;
              write(chromosome, partitionId, annotationDb, zipArchiveOutputStream);
            });
  }

  private void write(
      FuryFactory.Chromosome chromosome,
      int partitionId,
      FuryFactory.AnnotationDb annotationDb,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    // serialize and write to zip entry
    String zipArchiveEntryName = chromosome.getId() + "/ref/" + partitionId + ".zst";
    Logger.info("creating database partition %s", zipArchiveEntryName);
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
