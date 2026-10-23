package org.molgenis.vipannotate.annotation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryBufferFactory;
import org.molgenis.vipannotate.serialization.MemoryEncodable;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexDispatcherWriterTest {
  @Mock private MemoryBufferFactory memBufferFactory;
  @Mock private MemoryEncodable<AnnotationIndex<SequenceVariant>> indexSmallWriter;
  @Mock private MemoryEncodable<AnnotationIndex<SequenceVariant>> indexBigWriter;
  private SequenceVariantAnnotationIndexDispatcherWriter<SequenceVariant> indexWriter;

  @BeforeEach
  void setUp() {
    indexWriter = new SequenceVariantAnnotationIndexDispatcherWriter<>(memBufferFactory);
    indexWriter.register(EncodedSequenceVariant.Type.POS_20_BIT, indexSmallWriter);
    indexWriter.register(EncodedSequenceVariant.Type.BIG, indexBigWriter);
  }

  @Test
  void writeInto() {
    MemoryEncoder memEncoder = mock(MemoryEncoder.class);
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

    indexWriter.writeInto(index, memEncoder);
    InOrder inOrder = inOrder(indexSmallWriter, indexBigWriter);
    inOrder.verify(indexSmallWriter).writeInto(indexSmall, memEncoder);
    inOrder.verify(indexBigWriter).writeInto(indexBig, memEncoder);
  }
}
