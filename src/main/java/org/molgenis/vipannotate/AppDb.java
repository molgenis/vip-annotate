package org.molgenis.vipannotate;

import org.molgenis.streamvbyte.StreamVByteProvider;
import org.molgenis.vipannotate.annotation.fathmmmkl.FathmmMklAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.ncer.NcERAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.remm.RemmAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.annotation.spliceai.SpliceAiAnnotationDbBuilderCommand;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.zstd.ZstdProvider;

public class AppDb extends App {
  public static void main(String[] args) {
    try {
      AppDbArgs appArgs = new AppDbArgsParser().parse(args);

      if (appArgs.debugMode() != null && appArgs.debugMode()) {
        Logger.ENABLE_DEBUG_LOGGING = true;
      }

      getCommand(appArgs).run(appArgs.args());
    } catch (RuntimeException e) {
      handleException(e);
    } finally {
      StreamVByteProvider.INSTANCE.close();
      ZstdProvider.INSTANCE.close();
    }
  }

  private static Command getCommand(AppDbArgs appDbArgs) {
    return switch (appDbArgs.command()) {
      case FATHMM_MKL -> new FathmmMklAnnotationDbBuilderCommand();
      case GNOMAD_SHORT_VARIANT -> new GnomAdAnnotationDbBuilderCommand();
      case NCER -> new NcERAnnotationDbBuilderCommand();
      case PHYLOP -> new PhyloPAnnotationDbBuilderCommand();
      case REMM -> new RemmAnnotationDbBuilderCommand();
      case SPLICEAI -> new SpliceAiAnnotationDbBuilderCommand();
    };
  }
}
