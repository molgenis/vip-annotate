package org.molgenis.vipannotate.db.exact.format;

import org.apache.fury.memory.MemoryBuffer;

public interface AnnotationEncoder<T> {
  MemoryBuffer encode(T annotation);
}
