package org.molgenis.vcf.annotate.util;

import java.io.FilterOutputStream;
import java.io.OutputStream;

/** Helper class to ensure System.out is not closed */
public class CloseIgnoringOutputStream extends FilterOutputStream {
  public CloseIgnoringOutputStream(OutputStream outputStream) {
    super(outputStream);
  }

  @Override
  public void close() {
    // noop
  }
}
