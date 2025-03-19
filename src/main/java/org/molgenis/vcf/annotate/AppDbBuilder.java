package org.molgenis.vcf.annotate;

import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import java.io.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.molgenis.vcf.annotate.db.effect.AnnotationDbBuilder;
import org.molgenis.vcf.annotate.db.effect.AnnotationDbWriter;
import org.molgenis.vcf.annotate.db.effect.model.GenomeAnnotationDb;
import org.molgenis.vcf.annotate.db.gnomad.GnomAdAnnotationDbBuilder;
import org.molgenis.vcf.annotate.util.Logger;

public class AppDbBuilder {
  public static void main(String[] args) {
    File inputFile = new File(args[0]);
    File referenceFile = new File(args[1]);
    File outputFile = new File(args[2]);
    File gnomAdFile = new File(args[3]);

    ReferenceSequenceFile referenceSequenceFile =
        ReferenceSequenceFileFactory.getReferenceSequenceFile(referenceFile);

    Logger.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();
    GenomeAnnotationDb genomeAnnotationDb =
        new AnnotationDbBuilder(referenceSequenceFile).create(inputFile);

    try (ZipArchiveOutputStream zipArchiveOutputStream = createWriter(outputFile)) {
      new AnnotationDbWriter().create(genomeAnnotationDb, zipArchiveOutputStream);
      new GnomAdAnnotationDbBuilder().create(gnomAdFile, zipArchiveOutputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    long endCreateDb = System.currentTimeMillis();
    Logger.info("creating database done in %sms", endCreateDb - startCreateDb);
  }

  private static ZipArchiveOutputStream createWriter(File zipFile) throws FileNotFoundException {
    return new ZipArchiveOutputStream(
        new BufferedOutputStream(new FileOutputStream(zipFile), 1048576));
  }
}
