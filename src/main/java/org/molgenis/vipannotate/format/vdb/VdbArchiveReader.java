package org.molgenis.vipannotate.format.vdb;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.zstd.ZstdDecompressionContext;

public class VdbArchiveReader implements AutoCloseable {
  private final FileChannel alignedChannel;
  private final FileChannel unalignedChannel;
  private final ZstdDecompressionContext zstdContext;
  private final VdbArchiveMetadata archiveMetadata;
  private final VdbMemoryBufferFactory memBufferFactory;

  // one reusable aligned scratch buffer
  @Nullable private MemoryBuffer scratchBuffer;

  VdbArchiveReader(
      FileChannel alignedChannel,
      FileChannel unalignedChannel,
      ZstdDecompressionContext zstdContext,
      VdbMemoryBufferFactory memBufferFactory,
      VdbArchiveMetadataReader metadataReader) {
    this.alignedChannel = alignedChannel;
    this.unalignedChannel = unalignedChannel;
    this.zstdContext = zstdContext;
    this.memBufferFactory = memBufferFactory;

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
          FileChannel.open(vdbPath, StandardOpenOption.READ),
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
   */
  public void readEntryInto(int id, MemoryBuffer memBuffer) {
    VdbArchiveMetadata.Entry entry = archiveMetadata.getEntry(id);
    read(entry, memBuffer);
  }

  public MemoryBuffer readLastEntry() {
    VdbArchiveMetadata.Entry entry = archiveMetadata.getEntries().getLast();
    return read(entry, null);
  }

  /**
   * Read the last entry into the given memory buffer
   *
   * @param memBuffer memory buffer originating from {@link VdbMemoryBufferFactory}
   */
  public void readLastEntryInto(MemoryBuffer memBuffer) {
    VdbArchiveMetadata.Entry entry = archiveMetadata.getEntries().getLast();
    read(entry, memBuffer);
  }

  private MemoryBuffer read(VdbArchiveMetadata.Entry entry, @Nullable MemoryBuffer memBuffer) {
    return switch (entry.compression()) {
      case PLAIN -> readPlain(entry.offset(), entry.length(), memBuffer, entry.ioMode());
      case ZSTD -> readZstd(entry.offset(), entry.length(), memBuffer, entry.ioMode());
    };
  }

  private MemoryBuffer readMetadata() {
    // get file size
    long size;
    try {
      size = unalignedChannel.size();
    } catch (IOException e) {
      throw new VdbException(e);
    }

    // read footer
    long footerLength = Long.BYTES + Long.BYTES + Integer.BYTES;
    if (size < footerLength) {
      throw new VdbException("invalid vdb file");
    }

    long footerOffset = size - footerLength;
    long metadataOffset, metadataLength;
    MemoryBuffer memBuffer =
        readPlain(footerOffset, footerLength, getScratchBuffer(footerLength), IoMode.BUFFERED);
    // verify signature
    long pos = footerLength - Integer.BYTES;
    int signature = memBuffer.getInt(pos);
    if (signature != VdbArchive.VDB_SIGNATURE) {
      throw new VdbException("invalid vdb file: signature mismatch");
    }

    // get offset and length of vdb metadata
    pos -= Long.BYTES;
    metadataLength = memBuffer.getLong(pos);
    pos -= Long.BYTES;
    metadataOffset = memBuffer.getLong(pos);

    // decompress metadata
    return readZstd(metadataOffset, metadataLength, null, IoMode.BUFFERED);
  }

  private MemoryBuffer readPlain(
      long offset, long length, @Nullable MemoryBuffer memBuffer, IoMode ioMode) {
    // create or reuse aligned buffer
    MemoryBuffer dstBuffer = prepareWriteBuffer(memBuffer, length);

    // read aligned data
    FileChannel fileChannel =
        switch (ioMode) {
          case DIRECT -> alignedChannel;
          case BUFFERED -> unalignedChannel;
        };
    try {
      if (fileChannel.read(dstBuffer.getByteBuffer(), offset) == -1) {
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

  private MemoryBuffer readZstd(
      long offset, long length, @Nullable MemoryBuffer memBuffer, IoMode ioMode) {
    MemoryBuffer srcBuffer = readPlain(offset, length, getScratchBuffer(length), ioMode);
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
      scratchBuffer = memBufferFactory.newMemoryBuffer(minCapacity);
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
    ClosableUtils.closeAll(alignedChannel, unalignedChannel);
  }
}
