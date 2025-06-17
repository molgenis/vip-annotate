package org.molgenis.vipannotate.format.vcf;

import java.util.Iterator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VcfReader implements Iterator<VcfRecord>, AutoCloseable {
  @Getter @NonNull private final VcfHeader header;
  @NonNull private final VcfRecordIterator recordIterator;

  @Override
  public boolean hasNext() {
    return this.recordIterator.hasNext();
  }

  @Override
  public VcfRecord next() {
    return this.recordIterator.next();
  }

  @Override
  public void close() {
    this.recordIterator.close();
  }
}
