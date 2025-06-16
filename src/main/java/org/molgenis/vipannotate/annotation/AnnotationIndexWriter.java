package org.molgenis.vipannotate.annotation;

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
  @NonNull private final GenomePartitionDataWriter genomePartitionDataWriter;

  public void write(GenomePartitionKey genomePartitionKey, AnnotationIndex annotationIndex) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_CAPACITY)) {
      fury.serializeJavaObject(byteArrayOutputStream, annotationIndex);
      byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray();
      MemoryBuffer memoryBuffer =
          MemoryBuffer.fromByteArray(uncompressedByteArray, 0, uncompressedByteArray.length);
      genomePartitionDataWriter.write(genomePartitionKey, "idx", memoryBuffer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
