package org.molgenis.vipannotate.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class FastaIndex {
  private final Map<String, FastaIndexRecord> records;

  public FastaIndex() {
    records = new LinkedHashMap<>();
  }

  public void addRecord(FastaIndexRecord record) {
    records.put(record.name(), record);
  }

  public boolean notContainsReferenceSequence(String name) {
    return !records.containsKey(name);
  }
}
