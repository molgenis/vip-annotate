package org.molgenis.vipannotate.format.vdb;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.PartitionKey;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.ExceptionUtils;

/** {@link VdbArchiveWriter} with writes an index as the last entry. */
public class PartitionedVdbArchiveWriter implements BinaryPartitionWriter {
  private final VdbArchiveWriter archiveWriter;
  private final VdbArchiveIndexWriter indexWriter;
  private final VdbArchiveIndex rootIndex;

  @Nullable private PartitionKey activePartitionIndexKey;
  @Nullable private VdbArchiveIndex activePartitionIndex;
  @Nullable private MemoryBuffer scratchBuffer;

  PartitionedVdbArchiveWriter(VdbArchiveWriter archiveWriter, VdbArchiveIndexWriter indexWriter) {
    this.archiveWriter = archiveWriter;
    this.indexWriter = indexWriter;
    this.rootIndex = new VdbArchiveIndex();
  }

  public static PartitionedVdbArchiveWriter create(
      VdbArchiveWriter archiveWriter, VdbMemoryBufferFactory memBufferFactory) {
    VdbArchiveIndexWriter indexWriter = new VdbArchiveIndexWriter(memBufferFactory);
    return new PartitionedVdbArchiveWriter(archiveWriter, indexWriter);
  }

  @SuppressWarnings("NullAway")
  @Override
  public void write(
      PartitionKey partitionKey,
      String dataId,
      Compression compression,
      IoMode ioMode,
      MemoryBuffer memBuffer) {
    // get index partition
    PartitionKey indexKey = partitionKey.getIndexPartitionKey();

    if (activePartitionIndexKey == null) {
      // init
      activePartitionIndexKey = indexKey;
      activePartitionIndex = new VdbArchiveIndex();
    } else if (!activePartitionIndexKey.equals(indexKey)) {
      // write and reset
      writeActivePartitionIndex();
      activePartitionIndexKey = indexKey;
      activePartitionIndex.reset();
    }

    // write entry and update partition index
    int entryId = archiveWriter.createEntry(memBuffer, compression, ioMode);
    String entryName = partitionKey.getCanonicalNameForData(dataId);
    activePartitionIndex.addEntry(entryName, entryId);
  }

  @SuppressWarnings("NullAway")
  private void writeActivePartitionIndex() {
    if (activePartitionIndex != null && !activePartitionIndex.isEmpty()) {
      int entryId = writeIndex(activePartitionIndex);
      rootIndex.addEntry(activePartitionIndexKey.getCanonicalName(), entryId);
    }
  }

  private int writeIndex(VdbArchiveIndex archiveIndex) {
    // serialize index
    if (scratchBuffer == null) {
      scratchBuffer = indexWriter.writeTo(archiveIndex);
    } else {
      scratchBuffer.clear();
      indexWriter.writeInto(archiveIndex, scratchBuffer);
    }

    // write index to archive
    return archiveWriter.createEntry(scratchBuffer, Compression.ZSTD, IoMode.BUFFERED);
  }

  @Override
  public void close() {
    Throwable firstThrowable = null;

    // write last partition index and root index
    try {
      writeActivePartitionIndex();
      writeIndex(rootIndex);
    } catch (Throwable throwable) {
      firstThrowable = throwable;
    }

    // close resources
    try {
      ClosableUtils.closeAll(scratchBuffer, archiveWriter);
    } catch (Throwable throwable) {
      if (firstThrowable != null) {
        firstThrowable.addSuppressed(throwable);
      } else {
        firstThrowable = throwable;
      }
    }

    ExceptionUtils.handleThrowable(firstThrowable);
  }
}
