package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexWriter<T extends SequenceVariant>
    implements AutoCloseable {
  private static final int BUFFER_SIZE = 8388608; // 8 MB

  private final BinarySerializer<AnnotationIndex<T>> indexSerializer;
  private final BinaryPartitionWriter binaryPartitionWriter;
  @Nullable private MemoryBuffer reusableMemoryBuffer;

  public void write(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex) {
    if (reusableMemoryBuffer == null) {
      // buffer will grow automatically
      reusableMemoryBuffer = MemoryBuffer.allocate(BUFFER_SIZE);
    } else {
      reusableMemoryBuffer.rewind();
    }

    indexSerializer.writeTo(reusableMemoryBuffer, annotationIndex);
    binaryPartitionWriter.write(partitionKey, "idx", reusableMemoryBuffer);
  }

  @Override
  public void close() {
    if (reusableMemoryBuffer != null) {
      reusableMemoryBuffer.close();
    }
  }
}
