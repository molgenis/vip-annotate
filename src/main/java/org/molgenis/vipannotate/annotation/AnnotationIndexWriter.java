package org.molgenis.vipannotate.annotation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class AnnotationIndexWriter {
  private static final int BUFFER_CAPACITY = 8388608; // 8 MB

  private final Fury fury;
  private final BinaryPartitionWriter binaryPartitionWriter;

  public void write(Partition.Key partitionKey, AnnotationIndex annotationIndex) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_CAPACITY)) {
      fury.serializeJavaObject(byteArrayOutputStream, annotationIndex);
      byte[] uncompressedByteArray = byteArrayOutputStream.toByteArray();
      MemoryBuffer memoryBuffer =
          MemoryBuffer.fromByteArray(uncompressedByteArray, 0, uncompressedByteArray.length);
      binaryPartitionWriter.write(partitionKey, "idx", memoryBuffer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
