package org.molgenis.vipannotate;

import static org.molgenis.vipannotate.util.MemorySizeValidator.validateMemorySizes;

import java.nio.file.Path;
import org.apache.fory.logging.LoggerFactory;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.VcfAnnotator;
import org.molgenis.vipannotate.annotation.VcfAnnotatorFactory;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.AppException;
import org.molgenis.vipannotate.util.Logger;

public class AppAnnotate {
  public static void main(String[] args) {
    LoggerFactory.disableLogging(); // disable apache fory logging

    AppAnnotateArgs appAnnotateArgs = null;
    try {
      appAnnotateArgs = new AppAnnotateArgsParser().parse(args);
      validateMemorySizes();
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

    if (appAnnotateArgs.debugMode() != null && appAnnotateArgs.debugMode()) {
      Logger.ENABLE_DEBUG_LOGGING = true;
    }

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
    int exitStatus;
    if (e instanceof AppException appException) {
      Logger.error("%s", e.getMessage());
      if (debugMode != null && debugMode) {
        appException.printStackTrace(System.err);
      }
      exitStatus = appException.getErrorCode().getCode();
    } else {
      String message = e.getMessage();
      if (message != null) {
        Logger.error("%s", message);
      }
      e.printStackTrace(System.err);
      Logger.error("an unexpected error occurred");
      exitStatus = 1;
    }
    System.exit(exitStatus);
  }
}
