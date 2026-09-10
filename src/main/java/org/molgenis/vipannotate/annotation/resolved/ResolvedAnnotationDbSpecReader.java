package org.molgenis.vipannotate.annotation.resolved;

import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionReader;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class ResolvedAnnotationDbSpecReader {
  public ResolvedAnnotationDbSpec read(BinaryPartitionReader partitionReader) {
    try (MemoryBuffer memBuffer = partitionReader.read("spec")) {
      if (memBuffer == null) {
        throw new UncheckedIOException(new IOException("failed to read spec"));
      }
      memBuffer.rewind();

      return ResolvedAnnotationDbSpec.readFrom(new BinaryReader(memBuffer));
    }
  }

  public static ResolvedAnnotationDbSpecReader create() {
    return new ResolvedAnnotationDbSpecReader();
  }
}
