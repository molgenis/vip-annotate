package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryDecodable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexDispatcherReaderTest {
  @Mock private MemoryDecodable<AnnotationIndex<SequenceVariant>> indexSmallReader;
  @Mock private MemoryDecodable<AnnotationIndex<SequenceVariant>> indexBigReader;
  private SequenceVariantAnnotationIndexDispatcherReader<SequenceVariant> indexReader;

  @BeforeEach
  void setUp() {
    indexReader = new SequenceVariantAnnotationIndexDispatcherReader<>();
    indexReader.register(EncodedSequenceVariant.Type.POS_20_BIT, indexSmallReader);
    indexReader.register(EncodedSequenceVariant.Type.BIG, indexBigReader);
  }

  @Test
  void readFrom() {
    MemoryBuffer memBuffer = mock(MemoryBuffer.class);
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    when(memDecoder.unwrap()).thenReturn(memBuffer);

    @SuppressWarnings("unchecked")
    AnnotationIndex<SequenceVariant> indexSmall = mock(AnnotationIndex.class);
    when(indexSmallReader.readFrom(memDecoder)).thenReturn(indexSmall);
    @SuppressWarnings("unchecked")
    AnnotationIndex<SequenceVariant> indexBig = mock(SequenceVariantAnnotationIndexBig.class);
    when(indexBigReader.readFrom(memDecoder)).thenReturn(indexBig);
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        indexReader.readFrom(memDecoder);
    assertAll(
        () ->
            assertEquals(
                indexSmall, index.getAnnotationIndex(EncodedSequenceVariant.Type.POS_20_BIT)),
        () -> assertEquals(indexBig, index.getAnnotationIndex(EncodedSequenceVariant.Type.BIG)));
  }

  @Test
  void readInto() {
    MemoryBuffer memBuffer = mock(MemoryBuffer.class);
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    when(memDecoder.unwrap()).thenReturn(memBuffer);

    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall =
        mock(SequenceVariantAnnotationIndexSmall.class);
    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
        mock(SequenceVariantAnnotationIndexBig.class);

    when(index.getAnnotationIndex(EncodedSequenceVariant.Type.POS_20_BIT)).thenReturn(indexSmall);
    when(index.getAnnotationIndex(EncodedSequenceVariant.Type.BIG)).thenReturn(indexBig);

    indexReader.readInto(memDecoder, index);
    InOrder inOrder = inOrder(indexSmallReader, indexBigReader);
    inOrder.verify(indexSmallReader).readInto(memDecoder, indexSmall);
    inOrder.verify(indexBigReader).readInto(memDecoder, indexBig);
  }
}
