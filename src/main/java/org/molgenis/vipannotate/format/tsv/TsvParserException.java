package org.molgenis.vipannotate.format.tsv;

import java.io.Serial;

public class TsvParserException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public TsvParserException(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return "error parsing tsv: %s".formatted(super.getMessage());
  }
}
