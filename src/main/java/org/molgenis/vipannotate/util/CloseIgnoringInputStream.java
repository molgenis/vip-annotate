package org.molgenis.vipannotate.util;

import java.io.FilterInputStream;
import java.io.InputStream;

/** Helper class to ensure System.in is not closed */
public class CloseIgnoringInputStream extends FilterInputStream {
  public CloseIgnoringInputStream(InputStream inputStream) {
    super(inputStream);
  }

  @Override
  public void close() {
    // noop
  }
}
