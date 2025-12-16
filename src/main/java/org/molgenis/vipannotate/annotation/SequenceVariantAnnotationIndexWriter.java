package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;
import org.molgenis.vipannotate.util.ClosableUtils;

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
      indexWriter.writeInto(annotationIndex, reusableMemBuffer);
    }

    binaryPartitionWriter.write(
        partitionKey, "idx", Compression.ZSTD, IoMode.BUFFERED, reusableMemBuffer);
  }

  @Override
  public void close() {
    ClosableUtils.close(reusableMemBuffer);
  }
}
