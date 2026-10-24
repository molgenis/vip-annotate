package org.molgenis.vipannotate.annotation.spec;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.PartitionedMemoryWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class AnnotationDbSpecWriter {
  public void write(AnnotationDbSpec spec, PartitionedMemoryWriter partitionWriter) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(32 * 1024)) {
      spec.writeTo(MemoryEncoder.wrap(memBuffer));
      partitionWriter.write("spec", Compression.ZSTD, IoMode.BUFFERED, memBuffer);
    }
  }

  public static AnnotationDbSpecWriter create() {
    return new AnnotationDbSpecWriter();
  }
}
