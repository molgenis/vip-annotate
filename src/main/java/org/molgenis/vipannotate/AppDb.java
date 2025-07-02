package org.molgenis.vipannotate;

import java.util.Scanner;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.ncer.NcERAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.remm.RemmAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.spliceai.SpliceAiAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.util.Logger;

public class AppDb {
  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);
    System.out.println("Press enter to continue.....");
    s.nextLine();

    try {
      AppDbArgs appDbArgs = new AppDbArgsParser().parse(args);
      getCommand(appDbArgs).run(appDbArgs.args());
    } catch (Exception e) {
      handleException(e);
    }

    System.out.println("Press enter to continue.....");
    s.nextLine();
  }

  private static Command getCommand(AppDbArgs appDbArgs) {
    return switch (appDbArgs.command()) {
      case GNOMAD_SHORT_VARIANT -> new GnomAdAnnotationDbBuilderCommand();
      case NCER -> new NcERAnnotationDbBuilderCommand();
      case PHYLOP -> new PhyloPAnnotationDbBuilderCommand();
      case REMM -> new RemmAnnotationDbBuilderCommand();
      case SPLICEAI -> new SpliceAiAnnotationDbBuilderCommand();
    };
  }

  private static void handleException(Exception e) {
    String message = e.getMessage();
    if (message != null) {
      Logger.error("%s", message);
    }
    e.printStackTrace(System.err);
    System.exit(1);
  }
}
