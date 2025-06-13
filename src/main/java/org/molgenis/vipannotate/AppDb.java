package org.molgenis.vipannotate;

import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.ncer.NcERAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.remm.RemmAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.util.Logger;

public class AppDb {
  public static void main(String[] args) {
    try {
      AppDbArgs appDbArgs = new AppDbArgsParser().parse(args);
      getCommand(appDbArgs).run(appDbArgs.args());
    } catch (Exception e) {
      handleException(e);
    }
  }

  private static Command getCommand(AppDbArgs appDbArgs) {
    switch (appDbArgs.command()) {
      case GNOMAD_SHORT_VARIANT -> new GnomAdShortVariantAnnotationDbBuilderCommand();
      case NCER -> new NcERAnnotationDbBuilderCommand();
      case PHYLOP -> new PhyloPAnnotationDbBuilderCommand();
      case REMM -> new RemmAnnotationDbBuilderCommand();
    }
  }

  private static void handleException(Exception e) {
    Logger.error("%s", e.getMessage());
    e.printStackTrace(System.err);
    System.exit(1);
  }
}
