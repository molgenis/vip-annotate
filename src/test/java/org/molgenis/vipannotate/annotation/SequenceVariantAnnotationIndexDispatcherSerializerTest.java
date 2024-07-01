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
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@SuppressWarnings({"DataFlowIssue", "NullableProblems", "NullAway", "unchecked"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexDispatcherSerializerTest {
  @Mock private BinarySerializer<AnnotationIndex<SequenceVariant>> indexSmallSerializer;
  @Mock private BinarySerializer<AnnotationIndex<SequenceVariant>> indexBigSerializer;
  private SequenceVariantAnnotationIndexDispatcherSerializer<SequenceVariant> indexSerializer;

  @BeforeEach
  void setUp() {
    indexSerializer = new SequenceVariantAnnotationIndexDispatcherSerializer<>();
    indexSerializer.register(Type.SMALL, indexSmallSerializer);
    indexSerializer.register(Type.BIG, indexBigSerializer);
  }

  @Test
  void writeTo() {
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    SequenceVariantAnnotationIndexSmall<SequenceVariant> indexSmall =
        mock(SequenceVariantAnnotationIndexSmall.class);
    SequenceVariantAnnotationIndexBig<SequenceVariant> indexBig =
        mock(SequenceVariantAnnotationIndexBig.class);

    when(index.getAnnotationIndex(Type.SMALL)).thenReturn(indexSmall);
    when(index.getAnnotationIndex(Type.BIG)).thenReturn(indexBig);

    indexSerializer.writeTo(memoryBuffer, index);
    InOrder inOrder = inOrder(indexSmallSerializer, indexBigSerializer);
    inOrder.verify(indexSmallSerializer).writeTo(memoryBuffer, indexSmall);
    inOrder.verify(indexBigSerializer).writeTo(memoryBuffer, indexBig);
  }

  @Test
  void readFrom() {
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    AnnotationIndex<SequenceVariant> indexSmall = mock(AnnotationIndex.class);
    when(indexSmallSerializer.readFrom(memoryBuffer)).thenReturn(indexSmall);
    AnnotationIndex<SequenceVariant> indexBig = mock(SequenceVariantAnnotationIndexBig.class);
    when(indexBigSerializer.readFrom(memoryBuffer)).thenReturn(indexBig);
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        indexSerializer.readFrom(memoryBuffer);
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

    indexSerializer.readInto(memoryBuffer, index);
    InOrder inOrder = inOrder(indexSmallSerializer, indexBigSerializer);
    inOrder.verify(indexSmallSerializer).readInto(memoryBuffer, indexSmall);
    inOrder.verify(indexBigSerializer).readInto(memoryBuffer, indexBig);
  }
}
