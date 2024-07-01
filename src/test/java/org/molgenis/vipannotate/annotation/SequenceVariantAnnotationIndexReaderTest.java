package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.BinarySerializer;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@SuppressWarnings({"DataFlowIssue", "NullAway", "NullableProblems", "unchecked"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexReaderTest {
  @Mock private AnnotationBlobReader annotationBlobReader;
  @Mock private BinarySerializer<AnnotationIndex<SequenceVariant>> indexSerializer;

  private SequenceVariantAnnotationIndexReader<SequenceVariant>
      sequenceVariantAnnotationIndexReader;

  @BeforeEach
  void setUp() {
    sequenceVariantAnnotationIndexReader =
        new SequenceVariantAnnotationIndexReader<>(annotationBlobReader, indexSerializer);
  }

  @AfterEach
  void tearDown() {
    sequenceVariantAnnotationIndexReader.close();
  }

  @Test
  void read() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> indexDispatcher =
        mock(SequenceVariantAnnotationIndexDispatcher.class);

    when(annotationBlobReader.read(partitionKey)).thenReturn(memoryBuffer);
    when(indexSerializer.readFrom(memoryBuffer)).thenReturn(indexDispatcher);

    assertEquals(indexDispatcher, sequenceVariantAnnotationIndexReader.read(partitionKey));
  }

  @Test
  void readNotExists() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    AnnotationIndex<SequenceVariant> index = mock(AnnotationIndex.class);
    when(indexSerializer.readEmpty()).thenReturn(index);
    assertEquals(index, sequenceVariantAnnotationIndexReader.read(partitionKey));
  }

  @Test
  void readInto() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
    when(annotationBlobReader.read(partitionKey)).thenReturn(memoryBuffer);

    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    sequenceVariantAnnotationIndexReader.readInto(partitionKey, index);
    verify(indexSerializer).readInto(memoryBuffer, index);
  }

  @Test
  void readIntoNotExists() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    sequenceVariantAnnotationIndexReader.readInto(partitionKey, index);

    assertAll(() -> verify(index).reset(), () -> verifyNoInteractions(indexSerializer));
  }
}
