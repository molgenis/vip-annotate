package org.molgenis.vipannotate.annotation;

import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpec;
import org.molgenis.vipannotate.format.vdb.PartitionedVdbArchiveReader;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationSpecLoader {
  private final AnnotationSpecReader specReader;

  public AnnotationSpec load(PartitionedVdbArchiveReader archiveReader) {
    MemoryBuffer memoryBuffer = archiveReader.read("spec");
    if (memoryBuffer == null) {
      throw new UncheckedIOException(new IOException("failed to read spec"));
    }
    memoryBuffer.rewind();

    return specReader.readSpec(memoryBuffer);
  }

  public static AnnotationSpecLoader create() {
    return new AnnotationSpecLoader(AnnotationSpecReader.create());
  }
}
