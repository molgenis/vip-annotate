package org.molgenis.vipannotate.format.vcf;

import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VcfParser implements Iterator<List<VcfRecord>>, AutoCloseable {
  @Getter private final VcfHeader header;
  private final VcfRecordBatchIterator vcfRecordBatchIterator;

  @Override
  public boolean hasNext() {
    return this.vcfRecordBatchIterator.hasNext();
  }

  @Override
  public List<VcfRecord> next() {
    return this.vcfRecordBatchIterator.next();
  }

  @Override
  public void close() {
    this.vcfRecordBatchIterator.close();
  }
}
