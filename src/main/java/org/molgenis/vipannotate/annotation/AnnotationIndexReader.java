package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public interface AnnotationIndexReader<T extends Feature> extends AutoCloseable {
  /**
   * Returns the index associated with the given {@link PartitionKey}.
   *
   * @return index or {@code null}
   */
  @Nullable AnnotationIndex<T> read(PartitionKey partitionKey);

  /**
   * Reads the index associated with the given {@link PartitionKey} into the given object.
   *
   * @return {@code true} if index exists, {@code true} false otherwise.
   */
  boolean readInto(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex);

  @Override
  void close();
}
