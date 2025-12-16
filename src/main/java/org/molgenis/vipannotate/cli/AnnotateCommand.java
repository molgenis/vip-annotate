package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.molgenis.vipannotate.annotation.VcfAnnotator;
import org.molgenis.vipannotate.annotation.VcfAnnotatorFactory;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.Output;

public class AnnotateCommand implements Command {
  @Override
  public void run(String[] args) {
    AnnotateArgs annotateArgs = new AnnotateArgsParser().parse(args);

    Input inputVcf = annotateArgs.inputVcf();
    Path annotationsDir = annotateArgs.annotationsDir();
    Output outputVcf = annotateArgs.outputVcf();
    VcfType outputVcfType = annotateArgs.vcfType();

    if (outputVcf.path() == null) {
      // output vcf is written to System.out, redirect logs to System.err
      Logger.REDIRECT_STDOUT_TO_STDERR = true;
    }

    VcfAnnotatorFactory vcfAnnotatorFactory = VcfAnnotatorFactory.create();
    try (VcfAnnotator vcfAnnotator =
        vcfAnnotatorFactory.create(inputVcf, annotationsDir, outputVcf, outputVcfType)) {
      vcfAnnotator.annotate();
    }
  }
}
