package org.molgenis.vipannotate.format;

import org.jspecify.annotations.Nullable;

public interface Tokenizer<T> extends AutoCloseable {
  /**
   * @return next token or null if no more tokens
   */
  @Nullable CharSequence nextToken();

  @Override
  void close();
}
