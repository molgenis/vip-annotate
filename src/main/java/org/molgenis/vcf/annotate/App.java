package org.molgenis.vcf.annotate;

import java.nio.file.Path;
import org.molgenis.vcf.annotate.annotator.VcfAnnotator;
import org.molgenis.vcf.annotate.annotator.VcfAnnotatorFactory;
import org.molgenis.vcf.annotate.util.Logger;
import org.molgenis.vcf.annotate.vcf.VcfType;

public class App {
  public static void main(String[] args) throws Exception {
    AppArgs appArgs = null;
    try {
      appArgs = AppArgsParser.parse(args);
      run(appArgs);
    } catch (Exception e) {
      handleException(e, appArgs != null ? appArgs.debugMode() : null);
    }
  }

  private static void run(AppArgs appArgs) throws Exception {
    Path inputVcf = appArgs.inputVcf();
    Path annotationsDir = appArgs.annotationsDir();
    Path outputVcf = appArgs.outputVcf();
    VcfType outputVcfType = appArgs.vcfType();

    if (outputVcf == null) {
      // output vcf is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    try (VcfAnnotator vcfAnnotator =
        VcfAnnotatorFactory.create(inputVcf, annotationsDir, outputVcf, outputVcfType)) {
      vcfAnnotator.annotate();
    }
  }

  private static void handleException(Exception e, Boolean debugMode) {
    if (debugMode != null && debugMode) {
      Logger.error("%s", e.getMessage());
      e.printStackTrace(System.err);
    } else {
      Logger.error("something went wrong");
    }
    System.exit(1);
  }
}
