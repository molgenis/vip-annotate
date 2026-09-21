package org.molgenis.vipannotate.format;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface RecordReader<F extends Field, R extends Record<F>> extends AutoCloseableNoThrow {
  /**
   * Reads the next record.
   *
   * @return next record or {@code null} at end of input
   */
  @Nullable R read();

  /**
   * Reads the next record into {@code record}.
   *
   * <p>The supplied record is reused and must not be retained by the reader.
   *
   * @return {@code true} if a record was read, {@code false} at end of input
   */
  boolean readInto(R record);
}
