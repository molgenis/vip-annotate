package org.molgenis.vipannotate.util;

public interface AutoCloseableNoThrow extends AutoCloseable {
  @Override
  void close();
}
