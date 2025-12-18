package org.molgenis.vipannotate.format.bed;

import java.io.Serial;

public class BedParserException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public BedParserException(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return "error parsing bed: %s".formatted(super.getMessage());
  }
}
