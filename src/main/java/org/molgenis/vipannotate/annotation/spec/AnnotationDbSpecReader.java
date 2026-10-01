package org.molgenis.vipannotate.annotation.spec;

import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.PartitionedMemoryReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class AnnotationDbSpecReader {
  public AnnotationDbSpec readFrom(PartitionedMemoryReader partitionReader) {
    try (MemoryBuffer memBuffer = partitionReader.read("spec")) {
      if (memBuffer == null) {
        throw new UncheckedIOException(new IOException("failed to read spec"));
      }
      memBuffer.rewind();

      return AnnotationDbSpec.readFrom(MemoryDecoder.wrap(memBuffer));
    }
  }

  public static AnnotationDbSpecReader create() {
    return new AnnotationDbSpecReader();
  }
}
