package org.molgenis.vipannotate;

import java.nio.file.Path;
import org.apache.fury.logging.LoggerFactory;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.VcfAnnotator;
import org.molgenis.vipannotate.annotation.VcfAnnotatorFactory;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.Logger;

public class AppAnnotate {

  static {
    LoggerFactory.disableLogging(); // disable apache fury logging
  }

  public static void main(String[] args) {
    AppAnnotateArgs appAnnotateArgs = null;
    try {
      appAnnotateArgs = new AppAnnotateArgsParser().parse(args);
      run(appAnnotateArgs);
    } catch (Exception e) {
      handleException(e, appAnnotateArgs != null ? appAnnotateArgs.debugMode() : null);
    }
  }

  private static void run(AppAnnotateArgs appAnnotateArgs) {
    Path inputVcf = appAnnotateArgs.inputVcf();
    Path annotationsDir = appAnnotateArgs.annotationsDir();
    Path outputVcf = appAnnotateArgs.outputVcf();
    VcfType outputVcfType = appAnnotateArgs.vcfType();

    if (outputVcf == null) {
      // output vcf is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    try (VcfAnnotatorFactory vcfAnnotatorFactory = new VcfAnnotatorFactory()) {
      try (VcfAnnotator vcfAnnotator =
          vcfAnnotatorFactory.create(inputVcf, annotationsDir, outputVcf, outputVcfType)) {
        vcfAnnotator.annotate();
      }
    }
  }

  private static void handleException(Exception e, @Nullable Boolean debugMode) {
    if (debugMode != null && debugMode) {
      String message = e.getMessage();
      if (message != null) {
        Logger.error("%s", message);
      }
      e.printStackTrace(System.err);
    } else {
      Logger.error("something went wrong");
    }
    System.exit(1);
  }
}
