package org.molgenis.vipannotate.db.v2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationIndexWriter {
  private static final int BUFFER_CAPACITY = 8388608; // 8 MB

  @NonNull private final Fury fury;
  @NonNull private final ZipZstdCompressionContext zipZstdCompressionContext;

  public void write(GenomePartitionKey genomePartitionKey, AnnotationIndex annotationIndex) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_CAPACITY)) {
      fury.serializeJavaObject(byteArrayOutputStream, annotationIndex);
      byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray();
      MemoryBuffer memoryBuffer =
          MemoryBuffer.fromByteArray(uncompressedByteArray, 0, uncompressedByteArray.length);
      zipZstdCompressionContext.write(genomePartitionKey, "idx", memoryBuffer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
