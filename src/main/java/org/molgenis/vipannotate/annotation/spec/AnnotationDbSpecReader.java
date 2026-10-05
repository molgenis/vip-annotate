package org.molgenis.vipannotate.annotation.spec;

import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionReader;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationDbSpecReader {
  public AnnotationDbSpec readFrom(BinaryPartitionReader partitionReader) {
    try (MemoryBuffer memBuffer = partitionReader.read("spec")) {
      if (memBuffer == null) {
        throw new UncheckedIOException(new IOException("failed to read spec"));
      }
      memBuffer.rewind();

      return AnnotationDbSpec.readFrom(new BinaryReader(memBuffer));
    }
  }

  public static AnnotationDbSpecReader create() {
    return new AnnotationDbSpecReader();
  }
}
