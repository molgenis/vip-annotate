package org.molgenis.vipannotate.format.bed;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.BufferedLineReader;
import org.molgenis.vipannotate.util.ClosableUtils;

public class BedFeatureIterator implements Iterator<BedFeature>, AutoCloseableNoThrow {
  private static final int BUFFER_SIZE_STRING_BUILDER = 256;

  private final BufferedLineReader reader;
  private final BedFeature reusableFeature;
  private final StringBuilder lineBuffer;

  private boolean eof = false;
  @Nullable private BedFeature nextFeature;

  public BedFeatureIterator(BufferedLineReader reader, BedFeature reusableFeature) {
    this.reader = reader;
    this.reusableFeature = reusableFeature;
    this.lineBuffer = new StringBuilder(BUFFER_SIZE_STRING_BUILDER);
  }

  @Override
  public boolean hasNext() {
    if (eof) {
      return false;
    }

    if (nextFeature != null) {
      return true;
    }

    while (true) {
      lineBuffer.setLength(0);

      int nrCharsRead = reader.readLineInto(lineBuffer);
      if (nrCharsRead == -1) {
        eof = true;
        return false;
      }

      if (!lineBuffer.isEmpty() && lineBuffer.charAt(0) != '#') {
        reusableFeature.reset(lineBuffer);
        nextFeature = reusableFeature;
        return true;
      }
    }
  }

  @SuppressWarnings("NullAway")
  @Override
  public BedFeature next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    BedFeature feature = nextFeature;
    nextFeature = null; // consume
    return feature;
  }

  @Override
  public void close() {
    ClosableUtils.close(reader);
  }
}
