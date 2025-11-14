package org.molgenis.vipannotate.format.vdb;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.zstd.ZstdDecompressionContext;
import org.molgenis.zstd.ZstdProvider;

@RequiredArgsConstructor
public class VdbArchiveReaderFactory {
  private final ZstdDecompressionContext decompressionContext;
  private final VdbMemoryBufferFactory memBufferFactory;
  private final VdbArchiveMetadataReader metadataReader;

  public static VdbArchiveReaderFactory create() {
    return new VdbArchiveReaderFactory(
        ZstdProvider.INSTANCE.get().createDecompressionContext(),
        new VdbMemoryBufferFactory(),
        new VdbArchiveMetadataReader());
  }

  public VdbArchiveReader create(Path archivePath) {
    if (Files.notExists(archivePath)) {
      throw new IllegalArgumentException("'%s' does not exist".formatted(archivePath));
    }
    return VdbArchiveReader.create(
        archivePath, decompressionContext, memBufferFactory, metadataReader);
  }
}
