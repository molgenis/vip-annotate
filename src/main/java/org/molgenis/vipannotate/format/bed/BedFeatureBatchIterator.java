package org.molgenis.vipannotate.format.bed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.molgenis.vipannotate.util.BufferedLineReader;
import org.molgenis.vipannotate.util.ClosableUtils;

public class BedFeatureBatchIterator implements Iterator<List<BedFeature>>, AutoCloseable {
  private static final int BUFFER_SIZE_STRING_BUILDER = 256;

  private final BufferedLineReader reader;
  private final List<BedFeature> reusableBatch;
  private final int batchSize;
  private final List<StringBuilder> lineBuffers;

  private boolean eof = false;

  public BedFeatureBatchIterator(BufferedLineReader reader, List<BedFeature> reusableBatch) {
    this.reader = reader;
    this.reusableBatch = reusableBatch;
    this.batchSize = reusableBatch.size();
    this.lineBuffers = new ArrayList<>(batchSize);

    // init line buffers
    for (int i = 0; i < batchSize; i++) {
      lineBuffers.add(new StringBuilder(BUFFER_SIZE_STRING_BUILDER));
    }
  }

  @Override
  public boolean hasNext() {
    return !eof;
  }

  @Override
  public List<BedFeature> next() {
    if (eof) {
      throw new NoSuchElementException();
    }

    int i = 0;
    while (i < batchSize) {
      StringBuilder stringBuilder = lineBuffers.get(i);
      stringBuilder.setLength(0);

      int nrCharsRead = reader.readLineInto(stringBuilder);
      if (nrCharsRead == -1) {
        eof = true;
        break;
      }
      if (!stringBuilder.isEmpty() && stringBuilder.charAt(0) != '#') {
        reusableBatch.get(i++).reset(stringBuilder);
      }
    }

    return i == batchSize ? reusableBatch : reusableBatch.subList(0, i);
  }

  @Override
  public void close() {
    ClosableUtils.close(reader);
  }
}
