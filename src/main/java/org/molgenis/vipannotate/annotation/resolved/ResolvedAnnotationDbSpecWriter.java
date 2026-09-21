package org.molgenis.vipannotate.annotation.resolved;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class ResolvedAnnotationDbSpecWriter {
  private final ResolvedAnnotationDbSpecSerializer specSerializer;

  public void write(ResolvedAnnotationDbSpec spec, BinaryPartitionWriter partitionWriter) {
    MemoryBuffer memoryBuffer = specSerializer.serialize(spec);
    partitionWriter.write("spec", Compression.ZSTD, IoMode.BUFFERED, memoryBuffer);
  }

  public static ResolvedAnnotationDbSpecWriter create() {
    ResolvedAnnotationDbSpecSerializer specSerializer = ResolvedAnnotationDbSpecSerializer.create();
    return new ResolvedAnnotationDbSpecWriter(specSerializer);
  }
}
