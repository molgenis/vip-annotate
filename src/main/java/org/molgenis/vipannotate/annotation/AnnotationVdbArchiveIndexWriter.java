package org.molgenis.vipannotate.annotation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

@RequiredArgsConstructor
public class AnnotationVdbArchiveIndexWriter
    implements MemoryBufferWriter<AnnotationVdbArchiveIndex> {
  private final MemoryBufferFactory memBufferFactory;

  @Override
  public MemoryBuffer writeTo(AnnotationVdbArchiveIndex archiveIndex) {
    MemoryBuffer memBuffer = memBufferFactory.newMemoryBuffer(calcSerializedSize(archiveIndex));
    writeInto(archiveIndex, memBuffer);
    return memBuffer;
  }

  @Override
  public void writeInto(AnnotationVdbArchiveIndex archiveIndex, MemoryBuffer memBuffer) {
    Set<String> entryKeys = archiveIndex.getEntries().keySet();
    memBuffer.putIntUnchecked(entryKeys.size());
    for (String entryKey : entryKeys) {
      memBuffer.putByteArrayUnchecked(entryKey.getBytes(UTF_8));
    }
  }

  // TODO expensive and not necessary since MemoryBuffer is auto-growing (see ensureCapacity fixme)
  private static long calcSerializedSize(AnnotationVdbArchiveIndex archiveIndex) {
    long size = Integer.BYTES; // number of entries
    for (Map.Entry<String, Integer> entry : archiveIndex.getEntries().entrySet()) {
      byte[] bytes = entry.getKey().getBytes(UTF_8);
      size += bytes.length + Integer.BYTES; // array length + bytes
    }
    return size;
  }
}
