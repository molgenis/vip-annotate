package org.molgenis.vipannotate.cli;

import org.molgenis.vipannotate.annotation.AnnotationDbDownloader;

public class DbDownloadCommand implements Command {
  @Override
  public void run(String[] args) {
    DbDownloadArgs dbDownloadArgs = new DbDownloadArgsParser().parse(args);

    try (AnnotationDbDownloader dbDownloader = AnnotationDbDownloader.create()) {
      dbDownloader.download(dbDownloadArgs.outputDir(), dbDownloadArgs.force());
    }
  }
}
