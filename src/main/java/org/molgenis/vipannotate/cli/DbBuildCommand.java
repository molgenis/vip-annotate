package org.molgenis.vipannotate.cli;

import org.molgenis.vipannotate.annotation.fathmmmkl.FathmmMklAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.ncer.NcERAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.remm.RemmAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.spliceai.SpliceAiAnnotationDbBuilderCommand;

public class DbBuildCommand implements Command {
  @Override
  public void run(String[] args) {
    DbBuildArgs dbBuildArgs = new DbBuildArgsParser().parse(args);
    getSubCommand(dbBuildArgs).run(dbBuildArgs.args());
  }

  private static Command getSubCommand(DbBuildArgs dbBuildArgs) {
    return switch (dbBuildArgs.command()) {
      case FATHMM_MKL -> new FathmmMklAnnotationDbBuilderCommand();
      case GNOMAD_SHORT_VARIANT -> new GnomAdAnnotationDbBuilderCommand();
      case NCER -> new NcERAnnotationDbBuilderCommand();
      case PHYLOP -> new PhyloPAnnotationDbBuilderCommand();
      case REMM -> new RemmAnnotationDbBuilderCommand();
      case SPLICEAI -> new SpliceAiAnnotationDbBuilderCommand();
    };
  }
}
