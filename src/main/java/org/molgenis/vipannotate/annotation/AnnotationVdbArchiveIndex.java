package org.molgenis.vipannotate.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

// TODO instead of String use Long with long 2-byte chrId, 2-byte binId, 2 byte annId
@RequiredArgsConstructor
public class AnnotationVdbArchiveIndex {
  @Getter private final Map<String, Integer> entries;

  public @Nullable Integer getEntryId(String entryName) {
    return entries.get(entryName);
  }

  public static class AnnotationVdbArchiveIndexBuilder {
    private final Map<String, Integer> entries;

    public AnnotationVdbArchiveIndexBuilder() {
      entries = new LinkedHashMap<>();
    }

    public AnnotationVdbArchiveIndexBuilder(int nrEntries) {
      int capacity = (int) Math.ceil(nrEntries / 0.75f);
      entries = new LinkedHashMap<>(capacity);
    }

    public void addEntry(String key, int id) {
      entries.put(key, id);
    }

    public AnnotationVdbArchiveIndex build() {
      return new AnnotationVdbArchiveIndex(entries);
    }
  }
}
