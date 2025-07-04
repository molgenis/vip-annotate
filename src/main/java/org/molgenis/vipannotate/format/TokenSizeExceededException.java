package org.molgenis.vipannotate.format;

public class TokenSizeExceededException extends RuntimeException {
  public TokenSizeExceededException(int maxSize) {
    super("token size exceeds > %d bytes".formatted(maxSize));
  }
}
