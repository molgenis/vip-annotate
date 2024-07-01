package org.molgenis.vipannotate.db.v3;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.exact.Variant;

@RequiredArgsConstructor
public class AnnotationDbImpl implements AnnotationDb<String> {
  private final AnnotationDbFileArchive annotationDbFileArchive;

  @Override
  public String findAnnotations(Variant variant) throws IOException {
    CompositePartitionKey partitionKey = annotationDbFileArchive.getPartition(variant);
    partitionKey.keyName();
    MemoryBuffer memoryBufferIndex = annotationDbFileArchive.getIndex(variant);
    if (1 == 1) throw new RuntimeException("FIXME implement"); // FIXME implement
    return "";
  }

  @Override
  public void close() {}
}
