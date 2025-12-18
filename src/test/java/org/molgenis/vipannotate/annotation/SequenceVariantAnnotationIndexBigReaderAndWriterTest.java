package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexBigReaderAndWriterTest {
  @Mock MemoryBufferFactory memoryBufferFactory;
  @Mock private SequenceVariantEncoder<SequenceVariant> encoder;
  private SequenceVariantAnnotationIndexBigReader<SequenceVariant>
      sequenceVariantAnnotationIndexBigReader;
  private SequenceVariantAnnotationIndexBigWriter<SequenceVariant>
      sequenceVariantAnnotationIndexBigWriter;

  @BeforeEach
  void setUp() {
    sequenceVariantAnnotationIndexBigReader =
        new SequenceVariantAnnotationIndexBigReader<>(encoder);
    sequenceVariantAnnotationIndexBigWriter =
        new SequenceVariantAnnotationIndexBigWriter<>(memoryBufferFactory);
  }

  @Test
  void testWriteIntoAndReadFrom() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
        new SequenceVariantAnnotationIndexBig<>(
            encoder,
            new BigInteger[] {
              BigInteger.ZERO, BigInteger.ONE, new BigInteger("18446744073709551616")
            });
    sequenceVariantAnnotationIndexBigWriter.writeInto(indexBig, memoryBuffer);
    memoryBuffer.rewind();
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBigDeserialized =
        sequenceVariantAnnotationIndexBigReader.readFrom(memoryBuffer);
    assertArrayEquals(
        indexBig.getEncodedVariantsArray(), indexBigDeserialized.getEncodedVariantsArray());
  }

  @Test
  void testWriteIntoAndReadInto() {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[100]);
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
        new SequenceVariantAnnotationIndexBig<>(
            encoder,
            new BigInteger[] {
              BigInteger.ZERO, BigInteger.ONE, new BigInteger("18446744073709551616")
            });
    sequenceVariantAnnotationIndexBigWriter.writeInto(indexBig, memoryBuffer);
    memoryBuffer.rewind();
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBigDeserialized =
        SequenceVariantAnnotationIndexBigFactory
            .create(); // TODO do not use factory in unit test class
    sequenceVariantAnnotationIndexBigReader.readInto(memoryBuffer, indexBigDeserialized);
    assertArrayEquals(
        indexBig.getEncodedVariantsArray(), indexBigDeserialized.getEncodedVariantsArray());
  }
}
