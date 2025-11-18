package org.molgenis.vipannotate.format.vdb;

import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VdbArchiveMetadata {
  @Getter private final List<Entry> entries;

  public Entry getEntry(int id) {
    if (id < 0 || id >= entries.size()) {
      throw new VdbException("invalid vdb entry id '%d'".formatted(id));
    }
    return entries.get(id);
  }

  public record Entry(long offset, long length, Compression compression, IoMode ioMode) {}

  public static class VdbArchiveMetadataBuilder {
    private final List<Entry> entries;

    public VdbArchiveMetadataBuilder() {
      entries = new ArrayList<>();
    }

    public VdbArchiveMetadataBuilder(int initialCapacity) {
      entries = new ArrayList<>(initialCapacity);
    }

    /** Returns generated entry metadata id */
    public int addEntry(long offset, long length, Compression compression, IoMode ioMode) {
      entries.add(new Entry(offset, length, compression, ioMode));
      return entries.size() - 1;
    }

    public VdbArchiveMetadata build() {
      return new VdbArchiveMetadata(entries);
    }
  }
}
