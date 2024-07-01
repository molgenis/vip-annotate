package org.molgenis.vipannotate;

import static org.molgenis.vipannotate.MemorySizeValidator.validateMemorySizes;

import java.nio.file.Path;
import org.molgenis.streamvbyte.StreamVByteProvider;
import org.molgenis.vipannotate.annotation.VcfAnnotator;
import org.molgenis.vipannotate.annotation.VcfAnnotatorFactory;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Output;
import org.molgenis.zstd.ZstdProvider;

public class AppAnnotate extends App {
  public static void main(String[] args) {
    try {
      AppAnnotateArgs appArgs = new AppAnnotateArgsParser().parse(args);
      validateMemorySizes();
      run(appArgs);
    } catch (RuntimeException e) {
      handleException(e);
    } finally {
      StreamVByteProvider.INSTANCE.close();
      ZstdProvider.INSTANCE.close();
    }
  }

  private static void run(AppAnnotateArgs appAnnotateArgs) {
    Input inputVcf = appAnnotateArgs.inputVcf();
    Path annotationsDir = appAnnotateArgs.annotationsDir();
    Output outputVcf = appAnnotateArgs.outputVcf();
    VcfType outputVcfType = appAnnotateArgs.vcfType();

    if (appAnnotateArgs.debugMode() != null && appAnnotateArgs.debugMode()) {
      Logger.ENABLE_DEBUG_LOGGING = true;
    }

    if (outputVcf.path() == null) {
      // output vcf is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    try (VcfAnnotatorFactory vcfAnnotatorFactory = VcfAnnotatorFactory.create()) {
      try (VcfAnnotator vcfAnnotator =
          vcfAnnotatorFactory.create(inputVcf, annotationsDir, outputVcf, outputVcfType)) {
        vcfAnnotator.annotate();
      }
    }
  }
}
