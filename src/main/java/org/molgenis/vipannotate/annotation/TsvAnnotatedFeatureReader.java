package org.molgenis.vipannotate.annotation;

import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.format.tsv.TsvParser;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class TsvAnnotatedFeatureReader implements AnnotatedFeatureReader {
  private final TsvParser parser;

  /**
   * produced AnnotatedFeature must not retain the TsvRecord input, since it's reused between calls.
   */
  private final Function<TsvRecord, AnnotatedFeature<?, ?>> mapper;

  private @Nullable TsvRecord next;
  private boolean loaded;

  @Override
  public boolean hasNext() {
    loadNext();
    return next != null;
  }

  @Override
  public AnnotatedFeature<?, ?> next() {
    loadNext();

    if (next == null) {
      throw new NoSuchElementException();
    }

    AnnotatedFeature<?, ?> feature = mapper.apply(next);
    loaded = false;
    return feature;
  }

  private void loadNext() {
    if (loaded) {
      return;
    }

    if (next == null) {
      next = parser.read();
    } else if (!parser.readInto(next)) {
      next = null;
    }

    loaded = true;
  }

  @Override
  public void close() {
    ClosableUtils.close(parser);
  }
}
