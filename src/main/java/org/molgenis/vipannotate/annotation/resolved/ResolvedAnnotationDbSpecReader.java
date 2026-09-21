package org.molgenis.vipannotate.annotation.resolved;

import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class ResolvedAnnotationDbSpecReader {
  private final ResolvedAnnotationDbSpecDeserializer specDeserializer;

  public ResolvedAnnotationDbSpec read(BinaryPartitionReader partitionReader) {
    MemoryBuffer memoryBuffer = partitionReader.read("spec");
    if (memoryBuffer == null) {
      throw new UncheckedIOException(new IOException("failed to read spec"));
    }
    memoryBuffer.rewind();

    return specDeserializer.deserialize(memoryBuffer);
  }

  public static ResolvedAnnotationDbSpecReader create() {
    return new ResolvedAnnotationDbSpecReader(ResolvedAnnotationDbSpecDeserializer.create());
  }
}
