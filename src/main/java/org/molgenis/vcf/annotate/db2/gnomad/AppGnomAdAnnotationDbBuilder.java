package org.molgenis.vcf.annotate.db2.gnomad;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppGnomAdAnnotationDbBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppGnomAdAnnotationDbBuilder.class);

  public static void main(String[] args) {
    File inputFile = new File(args[0]);
    File outputFile = new File(args[1]);

    LOGGER.info("creating database ...");
    long startCreateDb = System.currentTimeMillis();
    new GnomAdAnnotationDbBuilder().create(inputFile, outputFile);
    long endCreateDb = System.currentTimeMillis();
    LOGGER.info("creating database done in {}ms", endCreateDb - startCreateDb);
  }
}
