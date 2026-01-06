package org.molgenis.vipannotate.cli;

/**
 * parsed database-build command-line arguments
 *
 * @param command command to run.
 * @param args unparsed command arguments.
 */
@SuppressWarnings("ArrayRecordComponent")
public record DbBuildArgs(Command command, String[] args) {
  public enum Command {
    FATHMM_MKL,
    GNOMAD_SHORT_VARIANT,
    NCER,
    PHYLOP,
    REMM,
    SPLICEAI
  }
}
