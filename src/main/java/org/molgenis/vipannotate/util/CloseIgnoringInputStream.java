package org.molgenis.vipannotate.util;

import java.io.FilterInputStream;
import java.io.InputStream;
import lombok.NonNull;

/** Helper class to ensure the System.in is not closed */
public class CloseIgnoringInputStream extends FilterInputStream {
  public CloseIgnoringInputStream(@NonNull InputStream inputStream) {
    super(inputStream);
  }

  @Override
  public void close() {
    // noop
  }
}
