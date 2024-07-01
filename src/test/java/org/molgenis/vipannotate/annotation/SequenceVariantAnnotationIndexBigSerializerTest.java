package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@SuppressWarnings({"DataFlowIssue", "NullAway", "NullableProblems"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexBigSerializerTest {
  @Mock private SequenceVariantEncoder<SequenceVariant> encoder;
  private SequenceVariantAnnotationIndexBigSerializer<SequenceVariant>
      sequenceVariantAnnotationIndexBigSerializer;

  @BeforeEach
  void setUp() {
    sequenceVariantAnnotationIndexBigSerializer =
        new SequenceVariantAnnotationIndexBigSerializer<>(encoder);
  }

  @Test
  void testWriteToAndReadFrom() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
        new SequenceVariantAnnotationIndexBig<>(
            encoder,
            new BigInteger[] {
              BigInteger.ZERO, BigInteger.ONE, new BigInteger("18446744073709551616")
            });
    sequenceVariantAnnotationIndexBigSerializer.writeTo(memoryBuffer, indexBig);
    memoryBuffer.rewind();
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBigDeserialized =
        sequenceVariantAnnotationIndexBigSerializer.readFrom(memoryBuffer);
    assertArrayEquals(
        indexBig.getEncodedVariantsArray(), indexBigDeserialized.getEncodedVariantsArray());
  }

  @Test
  void testWriteToAndReadInto() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
        new SequenceVariantAnnotationIndexBig<>(
            encoder,
            new BigInteger[] {
              BigInteger.ZERO, BigInteger.ONE, new BigInteger("18446744073709551616")
            });
    sequenceVariantAnnotationIndexBigSerializer.writeTo(memoryBuffer, indexBig);
    memoryBuffer.rewind();
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBigDeserialized =
        SequenceVariantAnnotationIndexBigFactory
            .create(); // TODO do not use factory in unit test class
    sequenceVariantAnnotationIndexBigSerializer.readInto(memoryBuffer, indexBigDeserialized);
    assertArrayEquals(
        indexBig.getEncodedVariantsArray(), indexBigDeserialized.getEncodedVariantsArray());
  }
}
