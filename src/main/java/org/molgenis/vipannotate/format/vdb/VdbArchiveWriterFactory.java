package org.molgenis.vipannotate.format.vdb;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.zstd.ZstdCompressionContext;
import org.molgenis.zstd.ZstdProvider;

@RequiredArgsConstructor
public class VdbArchiveWriterFactory {
  private final ZstdCompressionContext compressionContext;
  private final VdbMemoryBufferFactory memBufferFactory;
  private final VdbArchiveMetadataWriter metadataWriter;

  public static VdbArchiveWriterFactory create() {
    return create(new VdbMemoryBufferFactory());
  }

  public static VdbArchiveWriterFactory create(VdbMemoryBufferFactory memBufferFactory) {
    return new VdbArchiveWriterFactory(
        ZstdProvider.INSTANCE.get().createCompressionContext(),
        memBufferFactory,
        new VdbArchiveMetadataWriter());
  }

  public VdbArchiveWriter create(Path archivePath, boolean force) {
    return VdbArchiveWriter.create(
        archivePath, force, compressionContext, memBufferFactory, metadataWriter);
  }
}
