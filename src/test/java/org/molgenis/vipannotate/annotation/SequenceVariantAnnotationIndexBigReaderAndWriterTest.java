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
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

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
    try (MemoryBuffer memoryBuffer = MemoryBuffer.allocateHeap(100)) {
      SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
          new SequenceVariantAnnotationIndexBig<>(
              encoder,
              new BigInteger[] {
                BigInteger.ZERO, BigInteger.ONE, new BigInteger("18446744073709551616")
              });
      sequenceVariantAnnotationIndexBigWriter.writeInto(
          indexBig, MemoryEncoder.autoGrowing(memoryBuffer));
      memoryBuffer.rewind();
      SequenceVariantAnnotationIndexBig<SequenceVariant> indexBigDeserialized =
          sequenceVariantAnnotationIndexBigReader.readFrom(MemoryDecoder.wrap(memoryBuffer));
      assertArrayEquals(
          indexBig.getEncodedVariantsArray(), indexBigDeserialized.getEncodedVariantsArray());
    }
  }

  @Test
  void testWriteIntoAndReadInto() {
    try (MemoryBuffer memoryBuffer = MemoryBuffer.allocateHeap(100)) {
      SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
          new SequenceVariantAnnotationIndexBig<>(
              encoder,
              new BigInteger[] {
                BigInteger.ZERO, BigInteger.ONE, new BigInteger("18446744073709551616")
              });
      sequenceVariantAnnotationIndexBigWriter.writeInto(
          indexBig, MemoryEncoder.autoGrowing(memoryBuffer));
      memoryBuffer.rewind();
      SequenceVariantAnnotationIndexBig<SequenceVariant> indexBigDeserialized =
          new SequenceVariantAnnotationIndexBig<>(
              new SequenceVariantEncoderBig<>(), new BigInteger[0]);
      sequenceVariantAnnotationIndexBigReader.readInto(
          MemoryDecoder.wrap(memoryBuffer), indexBigDeserialized);
      assertArrayEquals(
          indexBig.getEncodedVariantsArray(), indexBigDeserialized.getEncodedVariantsArray());
    }
  }
}
