package org.molgenis.vipannotate.format.vdb;

import static lombok.AccessLevel.PACKAGE;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

public class VdbArchiveIndex {
  @Getter(PACKAGE)
  final Map<String, Integer> entryNameToIdMap;

  public VdbArchiveIndex() {
    this.entryNameToIdMap = new LinkedHashMap<>();
  }

  public VdbArchiveIndex(int nrEntries) {
    int initialCapacity = (int) Math.ceil(nrEntries / 0.75f);
    entryNameToIdMap = new LinkedHashMap<>(initialCapacity);
  }

  public boolean isEmpty() {
    return entryNameToIdMap.isEmpty();
  }

  public @Nullable Integer getEntryId(String entryName) {
    return entryNameToIdMap.get(entryName);
  }

  public void addEntry(String key, int id) {
    if (entryNameToIdMap.put(key, id) != null) {
      throw new IllegalArgumentException("index entry already exists");
    }
  }

  public void reset() {
    entryNameToIdMap.clear();
  }
}
