package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexSmallReaderAndWriterTest {
  private static StreamVByte STREAM_V_BYTE = StreamVByte.create();

  @Mock private SequenceVariantEncoder<SequenceVariant> encoder;
  @Mock private MemoryBufferFactory memBufferFactory;
  private SequenceVariantAnnotationIndexSmallReader<SequenceVariant> indexReader;
  private SequenceVariantAnnotationIndexSmallWriter<SequenceVariant> indexWriter;

  @BeforeAll
  static void beforeAll() {
    STREAM_V_BYTE = StreamVByte.create();
  }

  @AfterAll
  static void afterAll() {
    STREAM_V_BYTE.close();
  }

  @BeforeEach
  void setUp() {
    indexReader = new SequenceVariantAnnotationIndexSmallReader<>(encoder, STREAM_V_BYTE);
    indexWriter = new SequenceVariantAnnotationIndexSmallWriter<>(memBufferFactory, STREAM_V_BYTE);
  }

  @Test
  void testWriteIntoAndReadFrom() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[] {0, 1, 2, Integer.MAX_VALUE});
    indexWriter.writeInto(indexSmall, memoryBuffer);
    long afterWritePos = memoryBuffer.getPosition();

    memoryBuffer.flip();
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmallDeserialized =
        indexReader.readFrom(memoryBuffer);
    assertAll(
        () ->
            assertArrayEquals(
                indexSmall.getEncodedVariantsArray(),
                indexSmallDeserialized.getEncodedVariantsArray()),
        () -> assertEquals(afterWritePos, memoryBuffer.getPosition()));
  }

  @Test
  void testWriteIntoAndReadInto() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);

    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[] {0, 1, 2, Integer.MAX_VALUE});
    indexWriter.writeInto(indexSmall, memoryBuffer);
    long afterWritePos = memoryBuffer.getPosition();

    memoryBuffer.flip();
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmallDeserialized =
        SequenceVariantAnnotationIndexSmallFactory
            .create(); // TODO do not use factory in unit test class
    indexReader.readInto(memoryBuffer, indexSmallDeserialized);
    assertAll(
        () ->
            assertArrayEquals(
                indexSmall.getEncodedVariantsArray(),
                indexSmallDeserialized.getEncodedVariantsArray()),
        () -> assertEquals(afterWritePos, memoryBuffer.getPosition()));
  }
}
