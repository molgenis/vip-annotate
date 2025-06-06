package org.molgenis.vipannotate.vcf;

import java.io.Serial;
import lombok.NonNull;

public class VcfMetaInfoParserException extends VcfParserException {
  @Serial
  private static final long serialVersionUID = 1L;
  private final String line;

  public VcfMetaInfoParserException(@NonNull String line, @NonNull String message) {
    super(message);
    this.line = line;
  }

  @Override
  public String getMessage() {
    return "invalid vcf header line '%s': %s".formatted(line, super.getMessage());
  }
}
