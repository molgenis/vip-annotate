package org.molgenis.vipannotate.format.tsv;

import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.TsvIterator;

@RequiredArgsConstructor
public class TsvParser implements Iterator<String[]>, AutoCloseable {
  private final TsvIterator tsvIterator;

  @Override
  public boolean hasNext() {
    return this.tsvIterator.hasNext();
  }

  @Override
  public String[] next() {
    return this.tsvIterator.next();
  }

  @Override
  public void close() {
    ClosableUtils.close(tsvIterator);
  }
}
