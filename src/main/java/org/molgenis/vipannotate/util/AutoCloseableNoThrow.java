package org.molgenis.vipannotate.util;

// TODO replace project usages of AutoCloseable with AutoCloseableNoThrow
public interface AutoCloseableNoThrow extends AutoCloseable {
  @Override
  void close();
}
