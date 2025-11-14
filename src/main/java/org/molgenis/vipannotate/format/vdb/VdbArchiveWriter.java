package org.molgenis.vipannotate.format.vdb;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vdb.VdbArchiveMetadata.VdbArchiveMetadataBuilder;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.zstd.ZstdCompressionContext;

// TODO we could write entries at unaligned offsets but read aligned to keep db compact
public class VdbArchiveWriter implements AutoCloseable {
  private final FileChannel fileChannel;
  private final ZstdCompressionContext zstdContext;
  private final MemoryBufferFactory memBufferFactory;
  private final VdbArchiveMetadataWriter metadataWriter;
  private final VdbArchiveMetadataBuilder metadataBuilder;

  // one reusable aligned scratch buffer for all writes
  @Nullable private MemoryBuffer scratchBuffer;
  private long pos;

  VdbArchiveWriter(
      FileChannel fileChannel,
      ZstdCompressionContext zstdContext,
      MemoryBufferFactory memBufferFactory,
      VdbArchiveMetadataWriter metadataWriter) {
    this.fileChannel = fileChannel;
    this.zstdContext = zstdContext;
    this.memBufferFactory = memBufferFactory;
    this.metadataWriter = metadataWriter;
    this.metadataBuilder = new VdbArchiveMetadataBuilder();
  }

  @SuppressWarnings("DataFlowIssue")
  public static VdbArchiveWriter create(
      Path vdbPath,
      boolean force,
      ZstdCompressionContext zstdContext,
      VdbMemoryBufferFactory vdbMemoryBufferFactory,
      VdbArchiveMetadataWriter metadataWriter) {
    try {
      if (force && Files.exists(vdbPath)) {
        Files.delete(vdbPath);
      }

      return new VdbArchiveWriter(
          FileChannel.open(
              vdbPath,
              StandardOpenOption.CREATE_NEW,
              StandardOpenOption.WRITE,
              ExtendedOpenOption.DIRECT),
          zstdContext,
          vdbMemoryBufferFactory,
          metadataWriter);
    } catch (IOException e) {
      throw new VdbException(e);
    }
  }

  /** writes an entry to the archive and returns a generated entry id to read the entry */
  public int createEntry(MemoryBuffer memBuffer, CompressionMethod compressionMethod) {
    long offset = pos, length;
    MemoryBuffer dstBuffer = create(memBuffer, compressionMethod);
    try {
      length = dstBuffer.getPosition();
      putPadding(dstBuffer);

      dstBuffer.flip();
      write(dstBuffer);
    } finally {
      if (dstBuffer != scratchBuffer) {
        dstBuffer.close();
      }
    }
    return metadataBuilder.addEntry(offset, length, compressionMethod);
  }

  private MemoryBuffer create(MemoryBuffer memBuffer, CompressionMethod compressionMethod) {
    return switch (compressionMethod) {
      case PLAIN -> createPlain(memBuffer);
      case ZSTD -> createZstd(memBuffer);
    };
  }

  private MemoryBuffer createPlain(MemoryBuffer memBuffer) {
    MemoryBuffer dstMemBuffer = getScratchBuffer(memBuffer.getLimit());
    dstMemBuffer.copyFrom(memBuffer);
    dstMemBuffer.setPosition(memBuffer.getLimit());
    return dstMemBuffer;
  }

  private MemoryBuffer createZstd(MemoryBuffer memBuffer) {
    // determine max length of compressed data
    MemorySegment srcSegment = memBuffer.getMemSegment();
    long maxLength = zstdContext.compressBound(srcSegment);

    MemoryBuffer dstBuffer = getScratchBuffer(Long.BYTES + maxLength);
    dstBuffer.putLongUnchecked(memBuffer.getPosition());

    // write compressed data
    long length = zstdContext.compress(dstBuffer.getMemSegment(), srcSegment);
    dstBuffer.setPosition(Long.BYTES + length);
    dstBuffer.setLimit(Long.BYTES + length);
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

  /** write buffer to file */
  @SuppressWarnings("DataFlowIssue")
  private void write(MemoryBuffer memBuffer) {
    ByteBuffer byteBuffer = memBuffer.getMemSegment().asByteBuffer();
    try {
      while (byteBuffer.hasRemaining()) {
        pos += fileChannel.write(byteBuffer);
      }
    } catch (IOException e) {
      throw new VdbException(e);
    }
  }

  private void putPadding(MemoryBuffer memBuffer) {
    if (memBuffer.getLimit() % VdbArchive.VDB_BYTE_ALIGNMENT == 0) {
      return;
    }

    long padding =
        (VdbArchive.VDB_BYTE_ALIGNMENT - (memBuffer.getLimit() % VdbArchive.VDB_BYTE_ALIGNMENT))
            % VdbArchive.VDB_BYTE_ALIGNMENT;
    // mem buffer is aligned, so call to ensureCapacity is not needed
    memBuffer.setLimit(memBuffer.getLimit() + padding);
    putPadding(memBuffer, padding);
  }

  private void putPadding(MemoryBuffer memBuffer, long count) {
    memBuffer.putByte((byte) 0, count);
  }

  private void writeMetadataAndFooter() {
    try (MemoryBuffer srcBuffer = metadataWriter.writeTo(metadataBuilder.build())) {
      srcBuffer.flip();

      MemoryBuffer dstBuffer = createZstd(srcBuffer);
      long metadataOffset = pos;
      long metadataLength = dstBuffer.getPosition();

      // ensure buffer has space for padding and footer
      long footerLength = Long.BYTES + Long.BYTES + Integer.BYTES;
      long padding =
          (VdbArchive.VDB_BYTE_ALIGNMENT
                  - ((metadataLength + footerLength) % VdbArchive.VDB_BYTE_ALIGNMENT))
              % VdbArchive.VDB_BYTE_ALIGNMENT;
      dstBuffer.ensureCapacity(metadataLength + padding + footerLength);
      dstBuffer.setLimit(metadataLength + padding + footerLength);

      // write padding and footer to buffer
      putPadding(dstBuffer, padding);
      dstBuffer.putLong(metadataOffset);
      dstBuffer.putLong(metadataLength);
      dstBuffer.putInt(VdbArchive.VDB_SIGNATURE);

      dstBuffer.flip();
      write(dstBuffer);
    }
  }

  @Override
  public void close() {
    writeMetadataAndFooter();

    try {
      fileChannel.force(false);
      fileChannel.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (scratchBuffer != null) {
        scratchBuffer.close();
      }
    }
  }
}
