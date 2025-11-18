package org.molgenis.vipannotate.format.vdb;

import org.molgenis.vipannotate.annotation.PartitionKey;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

public interface BinaryPartitionWriter extends AutoCloseable {
  void write(
      PartitionKey key,
      String dataId,
      Compression compression,
      IoMode ioMode,
      MemoryBuffer memBuffer);

  @Override
  void close();
}
