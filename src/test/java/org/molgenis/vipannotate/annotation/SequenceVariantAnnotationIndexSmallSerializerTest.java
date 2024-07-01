package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.streamvbyte.StreamVByte;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@SuppressWarnings({"DataFlowIssue", "NullAway", "NullableProblems"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexSmallSerializerTest {
  @Mock private SequenceVariantEncoder<SequenceVariant> encoder;
  private SequenceVariantAnnotationIndexSmallSerializer<SequenceVariant>
      sequenceVariantAnnotationIndexSmallSerializer;

  @BeforeEach
  void setUp() {
    sequenceVariantAnnotationIndexSmallSerializer =
        new SequenceVariantAnnotationIndexSmallSerializer<>(encoder, StreamVByte.create());
  }

  @SuppressWarnings({"DataFlowIssue", "NullAway"})
  @Test
  void testWriteToAndReadFrom() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[] {0, 1, 2, Integer.MAX_VALUE});
    sequenceVariantAnnotationIndexSmallSerializer.writeTo(memoryBuffer, indexSmall);
    long afterWritePos = memoryBuffer.getPosition();

    memoryBuffer.rewind();
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmallDeserialized =
        sequenceVariantAnnotationIndexSmallSerializer.readFrom(memoryBuffer);
    assertAll(
        () ->
            assertArrayEquals(
                indexSmall.getEncodedVariantsArray(),
                indexSmallDeserialized.getEncodedVariantsArray()),
        () -> assertEquals(afterWritePos, memoryBuffer.getPosition()));
  }

  @Test
  void testWriteToAndReadInto() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);

    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall =
        new SequenceVariantAnnotationIndexSmall<>(encoder, new int[] {0, 1, 2, Integer.MAX_VALUE});
    sequenceVariantAnnotationIndexSmallSerializer.writeTo(memoryBuffer, indexSmall);
    long afterWritePos = memoryBuffer.getPosition();

    memoryBuffer.rewind();
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmallDeserialized =
        SequenceVariantAnnotationIndexSmallFactory
            .create(); // TODO do not use factory in unit test class
    sequenceVariantAnnotationIndexSmallSerializer.readInto(memoryBuffer, indexSmallDeserialized);
    assertAll(
        () ->
            assertArrayEquals(
                indexSmall.getEncodedVariantsArray(),
                indexSmallDeserialized.getEncodedVariantsArray()),
        () -> assertEquals(afterWritePos, memoryBuffer.getPosition()));
  }
}
