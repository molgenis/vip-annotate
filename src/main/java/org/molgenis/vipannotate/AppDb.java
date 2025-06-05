package org.molgenis.vipannotate;

import org.molgenis.vipannotate.db.chrpos.ncer.AppDbNcER;
import org.molgenis.vipannotate.db.chrpos.phylop.AppDbPhyloP;
import org.molgenis.vipannotate.db.chrpos.remm.AppDbRemm;
import org.molgenis.vipannotate.db.gnomad.AppDbGnomAd;
import org.molgenis.vipannotate.util.Logger;

public class AppDb {
  public static void main(String[] args) {
    try {
      AppDbArgs appDbArgs = new AppDbArgsParser().parse(args);
      run(appDbArgs);
    } catch (Exception e) {
      handleException(e);
    }
  }

  private static void run(AppDbArgs appDbArgs) {
    switch (appDbArgs.command()) {
      case GNOMAD -> AppDbGnomAd.main(appDbArgs.args());
      case NCER -> AppDbNcER.main(appDbArgs.args());
      case PHYLOP -> AppDbPhyloP.main(appDbArgs.args());
      case REMM -> AppDbRemm.main(appDbArgs.args());
    }
  }

  private static void handleException(Exception e) {
    Logger.error("%s", e.getMessage());
    e.printStackTrace(System.err);
    System.exit(1);
  }
}
