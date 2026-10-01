package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.PartitionedMemoryWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryEncodable;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndexWriter<T extends SequenceVariant>
    implements AutoCloseableNoThrow {
  private final MemoryEncodable<SequenceVariantAnnotationIndexDispatcher<T>> indexWriter;
  private final PartitionedMemoryWriter partitionedMemWriter;
  @Nullable private MemoryEncoder reusableMemEncoder;

  public void write(
      PartitionKey partitionKey, SequenceVariantAnnotationIndexDispatcher<T> annotationIndex) {
    if (reusableMemEncoder == null) {
      reusableMemEncoder = indexWriter.writeTo(annotationIndex);
    } else {
      reusableMemEncoder.unwrap().clear(); // FIXME
      indexWriter.writeInto(annotationIndex, reusableMemEncoder);
    }

    partitionedMemWriter.write(
        "idx", Compression.ZSTD, IoMode.BUFFERED, reusableMemEncoder.unwrap(), partitionKey);
  }

  @Override
  public void close() {
    if (reusableMemEncoder != null) {
      ClosableUtils.close(reusableMemEncoder.unwrap());
    }
  }
}
