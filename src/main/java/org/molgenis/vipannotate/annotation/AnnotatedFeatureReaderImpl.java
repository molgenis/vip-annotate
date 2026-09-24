package org.molgenis.vipannotate.annotation;

import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.Record;
import org.molgenis.vipannotate.format.RecordReader;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class AnnotatedFeatureReaderImpl<F extends Field, R extends Record<F>>
    implements AnnotatedFeatureReader {
  private final RecordReader<F, R> recordReader;

  /**
   * produced AnnotatedFeature must not retain the TsvRecord input, since it's reused between calls.
   */
  private final Function<R, AnnotatedFeature<?, ?>> mapper;

  private @Nullable R next;
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
      next = recordReader.read();
    } else if (!recordReader.readInto(next)) {
      next = null;
    }

    loaded = true;
  }

  @Override
  public void close() {
    ClosableUtils.close(recordReader);
  }
}
