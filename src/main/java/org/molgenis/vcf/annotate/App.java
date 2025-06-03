package org.molgenis.vcf.annotate;

import java.nio.file.Path;
import org.molgenis.vcf.annotate.annotator.VcfAnnotator;
import org.molgenis.vcf.annotate.annotator.VcfAnnotatorCreator;
import org.molgenis.vcf.annotate.util.Logger;

public class App {
  public static void main(String[] args) throws Exception {
    AppArgs appArgs = null;
    try {
      appArgs = AppArgsParser.parse(args);
      run(appArgs);
    } catch (Exception e) {
      if (appArgs != null && appArgs.debugMode() != null && appArgs.debugMode()) {
        Logger.error("%s", e.getMessage());
        e.printStackTrace(System.err);
      } else {
        Logger.error("something went wrong");
      }
      System.exit(1);
    }
  }

  private static void run(AppArgs appArgs) throws Exception {
    Path inputVcf = appArgs.inputVcf();
    Path annotationsZip = appArgs.annotationsZip();
    Path outputVcf = appArgs.outputVcf();

    if (outputVcf == null) {
      // output vcf is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    try (VcfAnnotator vcfAnnotator =
        VcfAnnotatorCreator.create(inputVcf, annotationsZip, outputVcf)) {
      vcfAnnotator.annotate();
    }
  }
}
