package org.molgenis.vipannotate.format.fasta;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class FastaIndex {
  private final Map<String, FastaIndexRecord> records;

  public FastaIndex() {
    records = new LinkedHashMap<>();
  }

  public void addRecord(FastaIndexRecord record) {
    records.put(record.name(), record);
  }

  public boolean containsReferenceSequence(String name) {
    return records.containsKey(name);
  }

  public @Nullable FastaIndexRecord get(String name) {
    return records.get(name);
  }
}
