package org.molgenis.vipannotate;

/**
 * @param command command to run.
 * @param args command arguments.
 */
public record AppDbArgs(Command command, String[] args) {
  public enum Command {
    GNOMAD_SHORT_VARIANT,
    NCER,
    PHYLOP,
    REMM,
    SPLICEAI
  }
}
