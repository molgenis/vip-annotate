package org.molgenis.vipannotate.util;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import lombok.NonNull;

/** Helper class to ensure the System.out is not closed */
public class CloseIgnoringOutputStream extends FilterOutputStream {
  public CloseIgnoringOutputStream(@NonNull OutputStream outputStream) {
    super(outputStream);
  }

  @Override
  public void close() {
    // noop
  }
}
