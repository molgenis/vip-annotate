package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexWriter<T extends SequenceVariant>
    implements AutoCloseable {
  private final MemoryBufferWriter<AnnotationIndex<T>> indexWriter;
  private final BinaryPartitionWriter binaryPartitionWriter;
  @Nullable private MemoryBuffer reusableMemBuffer;

  public void write(PartitionKey partitionKey, AnnotationIndex<T> annotationIndex) {
    if (reusableMemBuffer == null) {
      reusableMemBuffer = indexWriter.writeTo(annotationIndex);
    } else {
      reusableMemBuffer.clear();
    }

    indexWriter.writeInto(annotationIndex, reusableMemBuffer);
    reusableMemBuffer.flip();

    binaryPartitionWriter.write(partitionKey, "idx", reusableMemBuffer);
  }

  @Override
  public void close() {
    if (reusableMemBuffer != null) {
      reusableMemBuffer.close();
    }
  }
}
