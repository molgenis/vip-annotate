package org.molgenis.vipannotate.format.vcf;

import java.io.Serial;

public class VcfParserException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public VcfParserException(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return "error parsing vcf: %s".formatted(super.getMessage());
  }
}
