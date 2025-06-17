package org.molgenis.vipannotate.format.fasta;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;

public class FastaIndex {
  private final Map<String, FastaIndexRecord> records;

  public FastaIndex() {
    records = new LinkedHashMap<>();
  }

  public void addRecord(@NonNull FastaIndexRecord record) {
    records.put(record.name(), record);
  }

  public boolean containsReferenceSequence(@NonNull String name) {
    return records.containsKey(name);
  }

  public FastaIndexRecord get(@NonNull String name) {
    return records.get(name);
  }
}
