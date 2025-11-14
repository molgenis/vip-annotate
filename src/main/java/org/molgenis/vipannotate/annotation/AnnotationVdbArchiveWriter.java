package org.molgenis.vipannotate.annotation;

import static lombok.AccessLevel.PACKAGE;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.AnnotationVdbArchiveIndex.AnnotationVdbArchiveIndexBuilder;
import org.molgenis.vipannotate.format.vdb.CompressionMethod;
import org.molgenis.vipannotate.format.vdb.VdbArchiveWriter;
import org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor(access = PACKAGE)
public class AnnotationVdbArchiveWriter implements BinaryPartitionWriter {
  private final VdbArchiveWriter vdbArchiveWriter;
  private final AnnotationVdbArchiveIndexWriter indexWriter;
  private final AnnotationVdbArchiveIndexBuilder indexBuilder;

  public static AnnotationVdbArchiveWriter create(
      VdbArchiveWriter archiveWriter, VdbMemoryBufferFactory memBufferFactory) {
    return new AnnotationVdbArchiveWriter(
        archiveWriter,
        new AnnotationVdbArchiveIndexWriter(memBufferFactory),
        new AnnotationVdbArchiveIndexBuilder());
  }

  @Override
  public void write(PartitionKey partitionKey, String dataId, MemoryBuffer memBuffer) {
    // TODO dedup key building with reader
    String key = partitionKey.contig().getName() + "/" + partitionKey.bin() + "/" + dataId;
    int id = vdbArchiveWriter.createEntry(memBuffer, CompressionMethod.ZSTD);
    indexBuilder.addEntry(key, id);
  }

  private void writeIndex() {
    try (MemoryBuffer memBuffer = indexWriter.writeTo(indexBuilder.build())) {
      memBuffer.flip();
      vdbArchiveWriter.createEntry(memBuffer, CompressionMethod.ZSTD);
    }
  }

  @Override
  public void close() {
    writeIndex();
    vdbArchiveWriter.close();
  }
}
