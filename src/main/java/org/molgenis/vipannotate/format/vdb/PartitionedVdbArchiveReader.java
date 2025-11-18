package org.molgenis.vipannotate.format.vdb;

import static lombok.AccessLevel.PACKAGE;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.PartitionKey;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;

/** {@link VdbArchiveReader} with index stored as last entry. */
@RequiredArgsConstructor(access = PACKAGE)
public class PartitionedVdbArchiveReader implements BinaryPartitionReader {
  private final VdbArchiveReader archiveReader;
  private final VdbArchiveIndexReader indexReader;

  @Nullable private VdbArchiveIndex rootIndex;
  @Nullable private PartitionKey activePartitionIndexKey;
  @Nullable private VdbArchiveIndex activePartitionIndex;
  @Nullable private MemoryBuffer scratchBuffer;

  @Override
  public @Nullable MemoryBuffer read(PartitionKey partitionKey, String dataId) {
    updateActivePartitionIndex(partitionKey.getIndexPartitionKey());

    // load data
    if (activePartitionIndex != null) {
      String entryName = partitionKey.getCanonicalNameForData(dataId);
      Integer entryId = activePartitionIndex.getEntryId(entryName);
      if (entryId != null) {
        return archiveReader.readEntry(entryId);
      }
    }

    return null;
  }

  @Override
  public boolean readInto(PartitionKey partitionKey, String dataId, MemoryBuffer memBuffer) {
    updateActivePartitionIndex(partitionKey.getIndexPartitionKey());

    // load data
    if (activePartitionIndex != null) {
      String entryName = partitionKey.getCanonicalNameForData(dataId);
      Integer entryId = activePartitionIndex.getEntryId(entryName);
      if (entryId != null) {
        archiveReader.readEntryInto(entryId, memBuffer);
        return true;
      }
    }

    return false;
  }

  private void updateActivePartitionIndex(PartitionKey indexKey) {
    // fast path: no update required
    if (activePartitionIndexKey != null && activePartitionIndexKey.equals(indexKey)) {
      return;
    }

    // lazy load root index
    if (rootIndex == null) {
      if (scratchBuffer == null) {
        scratchBuffer = archiveReader.readLastEntry();
      } else {
        archiveReader.readLastEntryInto(scratchBuffer);
      }
      rootIndex = indexReader.readFrom(scratchBuffer);
    }

    // load partition index
    String entryId = indexKey.getCanonicalName();
    Integer indexEntryId = rootIndex.getEntryId(entryId);
    if (indexEntryId != null) {
      if (scratchBuffer == null) {
        scratchBuffer = archiveReader.readEntry(indexEntryId);
      } else {
        archiveReader.readEntryInto(indexEntryId, scratchBuffer);
      }
      activePartitionIndex = indexReader.readFrom(scratchBuffer);
    } else {
      activePartitionIndex = null;
    }

    activePartitionIndexKey = indexKey;
  }

  @Override
  public void close() {
    ClosableUtils.close(archiveReader);
  }
}
