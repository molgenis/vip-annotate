package org.molgenis.vipannotate.cli;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.App;

/**
 * Unparsed {@link App} command-line arguments
 *
 * @param debugMode whether to run the app in debug mode.
 * @param command command to run.
 * @param args command arguments.
 */
@SuppressWarnings("ArrayRecordComponent")
public record AppArgs(@Nullable Boolean debugMode, Command command, String[] args) {
  public enum Command {
    ANNOTATE,
    DATABASE_BUILD,
    DATABASE_DOWNLOAD
  }
}
