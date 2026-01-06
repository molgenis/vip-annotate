package org.molgenis.vipannotate.cli;

import java.io.Serial;
import org.molgenis.vipannotate.AppException;
import org.molgenis.vipannotate.util.ErrorCode;

public class ArgValidationException extends AppException {
  @Serial private static final long serialVersionUID = 1L;

  public ArgValidationException(String message) {
    super(ErrorCode.VALIDATION_ERROR, message);
  }
}
