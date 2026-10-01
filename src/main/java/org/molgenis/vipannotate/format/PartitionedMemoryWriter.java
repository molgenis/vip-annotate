package org.molgenis.vipannotate.format;

import org.molgenis.vipannotate.annotation.PartitionKey;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;

public interface PartitionedMemoryWriter extends AutoCloseableNoThrow {
  /**
   * Write a root entry
   *
   * @param entryId entry id
   * @param compression compression method
   * @param ioMode I/O mode
   * @param memBuffer memory buffer
   */
  void write(String entryId, Compression compression, IoMode ioMode, MemoryBuffer memBuffer);

  /**
   * Write a partition entry
   *
   * @param entryId entry id
   * @param compression compression method
   * @param ioMode I/O mode
   * @param memBuffer memory buffer
   * @param key partition key
   */
  void write(
      String entryId,
      Compression compression,
      IoMode ioMode,
      MemoryBuffer memBuffer,
      PartitionKey key);
}
