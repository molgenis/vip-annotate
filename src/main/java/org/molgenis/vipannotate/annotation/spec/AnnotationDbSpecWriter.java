package org.molgenis.vipannotate.annotation.spec;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.BinaryWriter;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationDbSpecWriter {
  public void write(AnnotationDbSpec spec, BinaryPartitionWriter partitionWriter) {
    try (MemoryBuffer memBuffer = MemoryBuffer.allocate(32 * 1024)) {
      spec.writeTo(BinaryWriter.autoGrowing(memBuffer));
      partitionWriter.write("spec", Compression.ZSTD, IoMode.BUFFERED, memBuffer);
    }
  }

  public static AnnotationDbSpecWriter create() {
    return new AnnotationDbSpecWriter();
  }
}
