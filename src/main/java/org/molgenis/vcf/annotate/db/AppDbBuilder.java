package org.molgenis.vcf.annotate.db;

import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import java.io.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.db.model.GenomeAnnotationDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppDbBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppDbBuilder.class);

  public static void main(String[] args) {
    File inputFile = new File(args[0]);
    File referenceFile = new File(args[1]);
    File outputFile = new File(args[2]);

    ReferenceSequenceFile referenceSequenceFile =
        ReferenceSequenceFileFactory.getReferenceSequenceFile(referenceFile);

    LOGGER.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();
    GenomeAnnotationDb genomeAnnotationDb =
        new AnnotationDbBuilder(referenceSequenceFile).create(inputFile);
    long endCreateDb = System.currentTimeMillis();
    LOGGER.info("creating database done in {}ms", endCreateDb - startCreateDb);

    LOGGER.info("writing database to file ...");
    long startWriteDb = System.currentTimeMillis();
    try (ZipArchiveOutputStream zipArchiveOutputStream = createWriter(outputFile)) {
      new AnnotationDbWriter().create(genomeAnnotationDb, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    long endWriteDb = System.currentTimeMillis();
    LOGGER.info("writing database to file done in {}ms", endWriteDb - startWriteDb);
  }

  private static ZipArchiveOutputStream createWriter(File zipFile) throws FileNotFoundException {
    return new ZipArchiveOutputStream(
        new BufferedOutputStream(new FileOutputStream(zipFile), 1048576));
  }
}
