package org.molgenis.vipannotate.annotation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.annotation.EncodedSequenceVariant.Type;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryBufferWriter;

@SuppressWarnings({"DataFlowIssue", "NullableProblems", "NullAway", "unchecked"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexDispatcherWriterTest {
  @Mock private MemoryBufferFactory memBufferFactory;
  @Mock private MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexSmallWriter;
  @Mock private MemoryBufferWriter<AnnotationIndex<SequenceVariant>> indexBigWriter;
  private SequenceVariantAnnotationIndexDispatcherWriter<SequenceVariant> indexWriter;

  @BeforeEach
  void setUp() {
    indexWriter = new SequenceVariantAnnotationIndexDispatcherWriter<>(memBufferFactory);
    indexWriter.register(Type.SMALL, indexSmallWriter);
    indexWriter.register(Type.BIG, indexBigWriter);
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

    indexWriter.writeInto(index, memoryBuffer);
    InOrder inOrder = inOrder(indexSmallWriter, indexBigWriter);
    inOrder.verify(indexSmallWriter).writeInto(indexSmall, memoryBuffer);
    inOrder.verify(indexBigWriter).writeInto(indexBig, memoryBuffer);
  }
}
