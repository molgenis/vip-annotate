package org.molgenis.vipannotate.annotation;

import java.nio.ByteBuffer;
import org.apache.fury.memory.MemoryBuffer;

public interface GenomePartitionDataReader {
  MemoryBuffer read(
      GenomePartitionKey genomePartitionKey, String dataId, ByteBuffer directByteBuffer);
}
