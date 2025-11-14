package org.molgenis.vipannotate.format.vdb;

import static org.molgenis.vipannotate.format.vdb.VdbArchive.VDB_BYTE_ALIGNMENT;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.zstd.ZstdDecompressionContext;

public class VdbArchiveReader implements AutoCloseable {
  private final FileChannel fileChannel;
  private final ZstdDecompressionContext zstdContext;
  private final VdbArchiveMetadata archiveMetadata;
  private final VdbMemoryBufferFactory memBufferFactory;

  // one reusable aligned scratch buffer for zstd reads
  @Nullable private MemoryBuffer scratchBuffer;

  VdbArchiveReader(
      FileChannel fileChannel,
      ZstdDecompressionContext zstdContext,
      VdbMemoryBufferFactory memBufferFactory,
      VdbArchiveMetadataReader metadataReader) {
    this.fileChannel = fileChannel;
    this.zstdContext = zstdContext;
    this.memBufferFactory = memBufferFactory;

    // TODO move to create
    // init
    try (MemoryBuffer memBuffer = readMetadata()) {
      memBuffer.flip();
      this.archiveMetadata = metadataReader.readFrom(memBuffer);
    }
  }

  @SuppressWarnings("DataFlowIssue")
  public static VdbArchiveReader create(
      Path vdbPath,
      ZstdDecompressionContext zstdContext,
      VdbMemoryBufferFactory vdbMemBufferFactory,
      VdbArchiveMetadataReader metadataReader) {
    try {
      return new VdbArchiveReader(
          FileChannel.open(vdbPath, StandardOpenOption.READ, ExtendedOpenOption.DIRECT),
          zstdContext,
          vdbMemBufferFactory,
          metadataReader);
    } catch (IOException e) {
      throw new VdbException(e);
    }
  }

  public MemoryBuffer readEntry(int id) {
    VdbArchiveMetadata.Entry entry = archiveMetadata.getEntry(id);
    return read(entry, null);
  }

  /**
   * Read entry into the given memory buffer
   *
   * @param id entry identifier
   * @param memBuffer memory buffer that originates from a call to {@link #readEntry}.
   * @return the given memory buffer
   */
  public MemoryBuffer readEntryInto(int id, MemoryBuffer memBuffer) {
    VdbArchiveMetadata.Entry entry = archiveMetadata.getEntry(id);
    return read(entry, memBuffer);
  }

  public MemoryBuffer readLastEntry() {
    VdbArchiveMetadata.Entry entry = archiveMetadata.getEntries().getLast();
    return read(entry, null);
  }

  /**
   * Read the last entry into the given memory buffer
   *
   * @param memBuffer memory buffer originating from {@link VdbMemoryBufferFactory}
   * @return the given memory buffer
   */
  public MemoryBuffer readLastEntryInto(MemoryBuffer memBuffer) {
    VdbArchiveMetadata.Entry entry = archiveMetadata.getEntries().getLast();
    return read(entry, memBuffer);
  }

  private MemoryBuffer read(VdbArchiveMetadata.Entry entry, @Nullable MemoryBuffer memBuffer) {
    return switch (entry.compressionMethod()) {
      case PLAIN -> readPlain(entry.offset(), entry.length(), memBuffer);
      case ZSTD -> readZstd(entry.offset(), entry.length(), memBuffer);
    };
  }

  // TODO perf: read metadata without direct io to allow page caching?
  private MemoryBuffer readMetadata() {
    // get file size
    long size;
    try {
      size = fileChannel.size();
    } catch (IOException e) {
      throw new VdbException(e);
    }
    if (size == 0) {
      throw new VdbException("invalid vdb file: file is empty");
    }
    if (size < VDB_BYTE_ALIGNMENT) {
      throw new VdbException(
          "invalid vdb file: file is less than %d bytes".formatted(VDB_BYTE_ALIGNMENT));
    }
    if (size % VDB_BYTE_ALIGNMENT != 0) {
      throw new VdbException("invalid vdb file");
    }

    // TODO reuse buffer
    // read last block
    long metadataOffset, metadataLength;
    try (MemoryBuffer memBuffer = readPlain(size - VDB_BYTE_ALIGNMENT, VDB_BYTE_ALIGNMENT, null)) {
      // verify signature
      long pos = VDB_BYTE_ALIGNMENT - Integer.BYTES;
      int signature = memBuffer.getInt(pos);
      if (signature != VdbArchive.VDB_SIGNATURE) {
        throw new VdbException("invalid vdb file: signature mismatch");
      }

      // get offset and length of vdb metadata
      pos -= Long.BYTES;
      metadataLength = memBuffer.getLong(pos);
      pos -= Long.BYTES;
      metadataOffset = memBuffer.getLong(pos);
    }

    // decompress metadata
    return readZstd(metadataOffset, metadataLength, null);
  }

  private MemoryBuffer readPlain(long offset, long length, @Nullable MemoryBuffer memBuffer) {
    // create or reuse aligned buffer
    MemoryBuffer dstBuffer = prepareWriteBuffer(memBuffer, length);

    // read aligned data
    try {
      if (fileChannel.read(dstBuffer.getMemSegment().asByteBuffer(), offset) == -1) {
        throw new VdbException("error reading vdb data");
      }
    } catch (IOException e) {
      throw new VdbException(e);
    }

    // set pos and limit to requested length
    dstBuffer.setPosition(length);
    dstBuffer.setLimit(length);
    return dstBuffer;
  }

  private MemoryBuffer readZstd(long offset, long length, @Nullable MemoryBuffer memBuffer) {
    MemoryBuffer srcBuffer = readPlain(offset, length, getScratchBuffer(length));
    srcBuffer.flip();

    long uncompressedLength = srcBuffer.getLong();
    MemoryBuffer dstBuffer = prepareWriteBuffer(memBuffer, uncompressedLength);
    zstdContext.decompress(dstBuffer.getMemSegment(), srcBuffer.getMemSegment());

    // set pos and limit to uncompressed length
    dstBuffer.setPosition(uncompressedLength);
    dstBuffer.setLimit(uncompressedLength);
    return dstBuffer;
  }

  private MemoryBuffer getScratchBuffer(long minCapacity) {
    if (scratchBuffer == null) {
      scratchBuffer = memBufferFactory.newMemoryBuffer();
    } else {
      scratchBuffer.ensureCapacity(minCapacity);
      scratchBuffer.clear();
    }
    return scratchBuffer;
  }

  private MemoryBuffer prepareWriteBuffer(@Nullable MemoryBuffer memBuffer, long minCapacity) {
    MemoryBuffer dstBuffer;
    if (memBuffer != null) {
      memBuffer.ensureCapacity(minCapacity);
      memBuffer.clear();
      dstBuffer = memBuffer;
    } else {
      dstBuffer = memBufferFactory.newMemoryBuffer(minCapacity);
    }
    return dstBuffer;
  }

  @Override
  public void close() {
    try {
      fileChannel.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
