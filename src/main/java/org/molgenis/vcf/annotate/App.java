package org.molgenis.vcf.annotate;

import java.io.*;
import java.nio.file.Path;
import org.apache.fury.logging.LoggerFactory;
import org.molgenis.vcf.annotate.annotator.VcfAnnotator;
import org.molgenis.vcf.annotate.annotator.VcfAnnotatorCreator;
import org.molgenis.vcf.annotate.util.Logger;

public class App {
  public static void main(String[] args) throws IOException {
    long start = System.currentTimeMillis();

    LoggerFactory.disableLogging(); // disable apache fury logging

    AppArgs appArgs = AppArgsParser.parse(args);

    Path inputVcf = appArgs.inputVcf();
    Path annotationsZip = appArgs.annotationsZip();
    Path outputVcf = appArgs.outputVcf();

    if (outputVcf == null) {
      // output vcf is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    long nrRecords;
    try (VcfAnnotator vcfAnnotator =
        VcfAnnotatorCreator.create(inputVcf, annotationsZip, outputVcf)) {
      nrRecords = vcfAnnotator.annotate();
    }

    long duration = System.currentTimeMillis() - start;
    Logger.info(
        "annotated %d vcf records in %d ms, %d records/s",
        nrRecords, duration, Math.round(nrRecords / (duration / 1000d)));
  }
}
