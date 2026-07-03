package org.molgenis.vipannotate.format.vdb;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.PartitionKey;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface BinaryPartitionReader extends AutoCloseableNoThrow {
  /**
   * Read a root entry
   *
   * @param entryId entry id
   * @return memory buffer or {@code null} if the entry does not exist
   */
  @Nullable MemoryBuffer read(String entryId);

  /**
   * Read a partition entry
   *
   * @param key partition key
   * @param entryId entry id
   * @return memory buffer or {@code null} if the entry does not exist
   */
  @Nullable MemoryBuffer read(PartitionKey key, String entryId);

  /**
   * Read a partition entry into the given memory buffer
   *
   * @param key partition key
   * @param entryId entry id
   * @return whether data was read into the given memory buffer
   */
  boolean readInto(PartitionKey key, String entryId, MemoryBuffer memBuffer);
}
