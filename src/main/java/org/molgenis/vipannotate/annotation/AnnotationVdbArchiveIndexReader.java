package org.molgenis.vipannotate.annotation;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.molgenis.vipannotate.annotation.AnnotationVdbArchiveIndex.AnnotationVdbArchiveIndexBuilder;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

public class AnnotationVdbArchiveIndexReader
    implements MemoryBufferReader<AnnotationVdbArchiveIndex> {

  @Override
  public AnnotationVdbArchiveIndex readFrom(MemoryBuffer memoryBuffer) {
    int nrEntries = memoryBuffer.getInt();
    AnnotationVdbArchiveIndexBuilder indexBuilder = new AnnotationVdbArchiveIndexBuilder(nrEntries);
    for (int i = 0; i < nrEntries; i++) {
      byte[] bytes = memoryBuffer.getByteArray();
      indexBuilder.addEntry(new String(bytes, UTF_8), i);
    }
    return indexBuilder.build();
  }

  @Override
  public void readInto(MemoryBuffer memoryBuffer, AnnotationVdbArchiveIndex object) {
    throw new RuntimeException("Not implemented"); // FIXME implement
  }
}
