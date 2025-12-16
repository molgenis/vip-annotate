package org.molgenis.vipannotate.format.vdb;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.PartitionKey;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

public interface BinaryPartitionReader extends AutoCloseable {
  /**
   * Read a partition entry
   *
   * @param key partition key
   * @param annId annotation id
   * @return memory buffer or {@code null} if the entry does not exist
   */
  @Nullable MemoryBuffer read(PartitionKey key, String annId);

  /**
   * Read a partition entry into the given memory buffer
   *
   * @param key partition key
   * @param annId annotation id
   * @return whether data was read into the given memory buffer
   */
  boolean readInto(PartitionKey key, String annId, MemoryBuffer memBuffer);

  @Override
  void close();
}
