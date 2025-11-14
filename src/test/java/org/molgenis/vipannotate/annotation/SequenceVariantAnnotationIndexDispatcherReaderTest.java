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
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferReader;

@SuppressWarnings({"DataFlowIssue", "NullableProblems", "NullAway", "unchecked"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexDispatcherReaderTest {
  @Mock private MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexSmallReader;
  @Mock private MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexBigReader;
  private SequenceVariantAnnotationIndexDispatcherReader<SequenceVariant> indexReader;

  @BeforeEach
  void setUp() {
    indexReader = new SequenceVariantAnnotationIndexDispatcherReader<>();
    indexReader.register(Type.SMALL, indexSmallReader);
    indexReader.register(Type.BIG, indexBigReader);
  }

  @Test
  void readFrom() {
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    AnnotationIndex<SequenceVariant> indexSmall = mock(AnnotationIndex.class);
    when(indexSmallReader.readFrom(memoryBuffer)).thenReturn(indexSmall);
    AnnotationIndex<SequenceVariant> indexBig = mock(SequenceVariantAnnotationIndexBig.class);
    when(indexBigReader.readFrom(memoryBuffer)).thenReturn(indexBig);
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        indexReader.readFrom(memoryBuffer);
    assertAll(
        () -> assertEquals(indexSmall, index.getAnnotationIndex(Type.SMALL)),
        () -> assertEquals(indexBig, index.getAnnotationIndex(Type.BIG)));
  }

  @Test
  void readInto() {
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall =
        mock(SequenceVariantAnnotationIndexSmall.class);
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
        mock(SequenceVariantAnnotationIndexBig.class);

    when(index.getAnnotationIndex(Type.SMALL)).thenReturn(indexSmall);
    when(index.getAnnotationIndex(Type.BIG)).thenReturn(indexBig);

    indexReader.readInto(memoryBuffer, index);
    InOrder inOrder = inOrder(indexSmallReader, indexBigReader);
    inOrder.verify(indexSmallReader).readInto(memoryBuffer, indexSmall);
    inOrder.verify(indexBigReader).readInto(memoryBuffer, indexBig);
  }
}
