package org.molgenis.vipannotate.format.vdb;

import static org.molgenis.vipannotate.format.vdb.VdbMemoryBufferFactory.alignedLength;

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
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.FileChannelUtils;
import org.molgenis.zstd.ZstdCompressionContext;

public class VdbArchiveWriter implements AutoCloseable {
  private final FileChannel alignedChannel;
  private final FileChannel unalignedChannel;
  private final ZstdCompressionContext zstdContext;
  private final VdbMemoryBufferFactory memBufferFactory;
  private final VdbArchiveMetadataWriter metadataWriter;
  private final VdbArchiveMetadataBuilder metadataBuilder;

  // one reusable aligned scratch buffer
  @Nullable private MemoryBuffer scratchBuffer;
  private long pos;

  VdbArchiveWriter(
      FileChannel alignedChannel,
      FileChannel unalignedChannel,
      ZstdCompressionContext zstdContext,
      VdbMemoryBufferFactory memBufferFactory,
      VdbArchiveMetadataWriter metadataWriter) {
    this.alignedChannel = alignedChannel;
    this.unalignedChannel = unalignedChannel;
    this.zstdContext = zstdContext;
    this.memBufferFactory = memBufferFactory;
    this.metadataWriter = metadataWriter;
    this.metadataBuilder = new VdbArchiveMetadataBuilder();
  }

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

      FileChannel alignedFileChannel =
          FileChannel.open(
              vdbPath,
              StandardOpenOption.CREATE_NEW,
              StandardOpenOption.WRITE,
              ExtendedOpenOption.DIRECT);

      // improve read performance by starting of with a 8GB file
      alignedFileChannel.truncate(1L << 33); // 8 GB

      FileChannel unalignedFileChannel = FileChannel.open(vdbPath, StandardOpenOption.WRITE);
      return new VdbArchiveWriter(
          alignedFileChannel,
          unalignedFileChannel,
          zstdContext,
          vdbMemoryBufferFactory,
          metadataWriter);
    } catch (IOException e) {
      throw new VdbException(e);
    }
  }

  /** writes an entry to the archive. returns a generated entry id to read the entry */
  public int createEntry(MemoryBuffer memBuffer) {
    return createEntry(memBuffer, Compression.ZSTD);
  }

  /**
   * writes an entry to the archive using the given {@link Compression}. returns a generated entry
   * id to read the entry
   */
  public int createEntry(MemoryBuffer memBuffer, Compression compression) {
    return createEntry(memBuffer, compression, IoMode.DIRECT);
  }

  /**
   * writes an entry to the archive using the given {@link Compression} and {@link IoMode}. returns
   * a generated entry id to read the entry
   */
  public int createEntry(MemoryBuffer memBuffer, Compression compression, IoMode ioMode) {

    if (ioMode == IoMode.DIRECT) {
      // write padding to align position
      long padding = alignedLength(pos) - pos;
      if (padding > 0) {
        MemoryBuffer dstMemBuffer = getScratchBuffer(padding);
        dstMemBuffer.putByteUnchecked((byte) 0, padding);

        dstMemBuffer.flip();
        write(dstMemBuffer, IoMode.BUFFERED);
      }
    }

    MemoryBuffer dstBuffer = create(memBuffer, compression, ioMode);
    try {
      long offset = pos;
      long length = dstBuffer.getPosition();

      if (ioMode == IoMode.DIRECT) {
        // write padding to ensure writing number of bytes in multiples of alignment
        long padding = alignedLength(offset + length) - (offset + length);
        if (padding > 0) {
          dstBuffer.putByteUnchecked((byte) 0, padding);
        }
      }

      dstBuffer.flip();
      write(dstBuffer, ioMode);
      return metadataBuilder.addEntry(offset, length, compression, ioMode);
    } finally {
      if (dstBuffer != scratchBuffer) {
        dstBuffer.close();
      }
    }
  }

  private MemoryBuffer create(MemoryBuffer memBuffer, Compression compression, IoMode ioMode) {
    return switch (compression) {
      case PLAIN -> createPlain(memBuffer, ioMode);
      case ZSTD -> createZstd(memBuffer);
    };
  }

  private MemoryBuffer createPlain(MemoryBuffer memBuffer, IoMode ioMode) {
    if (ioMode == IoMode.BUFFERED || VdbMemoryBufferFactory.isAligned(memBuffer)) {
      return memBuffer;
    }

    // copy unaligned input buffer to aligned buffer
    memBuffer.flip();
    MemoryBuffer dstMemBuffer = getScratchBuffer(memBuffer.getLimit());
    dstMemBuffer.copyFrom(memBuffer);
    return dstMemBuffer;
  }

  private MemoryBuffer createZstd(MemoryBuffer memBuffer) {
    memBuffer.flip();

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
  private void write(MemoryBuffer memBuffer, IoMode ioMode) {
    FileChannel fileChannel =
        switch (ioMode) {
          case DIRECT -> alignedChannel;
          case BUFFERED -> unalignedChannel;
        };

    ByteBuffer byteBuffer = memBuffer.getByteBuffer();
    try {
      while (byteBuffer.hasRemaining()) {
        pos += fileChannel.write(byteBuffer, pos);
      }
    } catch (IOException e) {
      throw new VdbException(e);
    }
  }

  private void writeMetadataAndFooter() {
    VdbArchiveMetadata vdbArchiveMetadata = metadataBuilder.build();

    // write metadata
    long minCapacity = VdbArchiveMetadataWriter.calcSerializedSize(vdbArchiveMetadata);
    try (MemoryBuffer srcBuffer = memBufferFactory.newMemoryBuffer(minCapacity)) {
      metadataWriter.writeInto(vdbArchiveMetadata, srcBuffer);

      MemoryBuffer dstBuffer = createZstd(srcBuffer);
      long metadataOffset = pos;
      long metadataLength = dstBuffer.getPosition();
      long footerLength = Long.BYTES + Long.BYTES + Integer.BYTES;

      dstBuffer.ensureCapacity(metadataLength + footerLength);
      dstBuffer.setLimit(metadataLength + footerLength);

      // write footer
      dstBuffer.putLongUnchecked(metadataOffset);
      dstBuffer.putLongUnchecked(metadataLength);
      dstBuffer.putIntUnchecked(VdbArchive.VDB_SIGNATURE);

      dstBuffer.flip();
      write(dstBuffer, IoMode.BUFFERED);
    }
  }

  @Override
  public void close() {
    Throwable firstThrowable = null;

    // write metadata and footer
    try {
      writeMetadataAndFooter();
    } catch (Throwable throwable) {
      firstThrowable = throwable;
    }

    // truncate to pos
    try {
      alignedChannel.truncate(pos);
    } catch (Throwable throwable) {
      firstThrowable = throwable;
    }

    // force channels
    try {
      FileChannelUtils.forceAll(alignedChannel, unalignedChannel);
    } catch (Throwable throwable) {
      if (firstThrowable != null) {
        firstThrowable.addSuppressed(throwable);
      } else {
        firstThrowable = throwable;
      }
    }

    // close channels
    try {
      ClosableUtils.closeAll(alignedChannel, unalignedChannel, scratchBuffer);
    } catch (Throwable throwable) {
      if (firstThrowable != null) {
        firstThrowable.addSuppressed(throwable);
      } else {
        firstThrowable = throwable;
      }
    }

    // throw exception
    if (firstThrowable != null) {
      switch (firstThrowable) {
        case RuntimeException runtimeException -> throw runtimeException; // throw as is
        case Error error -> throw error; // throw as is
        default -> throw new RuntimeException(firstThrowable);
      }
    }
  }
}
