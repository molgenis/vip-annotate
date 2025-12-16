package org.molgenis.vipannotate;

import org.jspecify.annotations.Nullable;

/**
 * Unparsed {@link AppDb} command-line arguments
 *
 * @param debugMode whether to run the app in debug mode.
 * @param command command to run.
 * @param args command arguments.
 */
@SuppressWarnings("ArrayRecordComponent")
public record AppDbArgs(@Nullable Boolean debugMode, Command command, String[] args) {
  public enum Command {
    FATHMM_MKL,
    GNOMAD_SHORT_VARIANT,
    NCER,
    PHYLOP,
    REMM,
    SPLICEAI
  }
}
