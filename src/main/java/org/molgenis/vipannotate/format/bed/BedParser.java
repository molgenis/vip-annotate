package org.molgenis.vipannotate.format.bed;

import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.ClosableUtils;

/** <a href="https://samtools.github.io/hts-specs/BEDv1.pdf">.bed</a> file parser */
@RequiredArgsConstructor
public class BedParser implements Iterator<BedFeature>, AutoCloseable {
  private final BedFeatureIterator bedFeatureIterator;

  @Override
  public boolean hasNext() {
    return this.bedFeatureIterator.hasNext();
  }

  @Override
  public BedFeature next() {
    return this.bedFeatureIterator.next();
  }

  @Override
  public void close() {
    ClosableUtils.close(bedFeatureIterator);
  }
}
