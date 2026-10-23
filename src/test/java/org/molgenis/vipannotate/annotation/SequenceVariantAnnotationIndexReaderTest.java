package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.serialization.MemoryDecodable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@SuppressWarnings({"unchecked"})
@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationIndexReaderTest {
  @Mock private PartitionedEntryReader entryReader;

  @Mock
  private MemoryDecodable<SequenceVariantAnnotationIndexDispatcher<SequenceVariant>> indexReader;

  private SequenceVariantAnnotationIndexReader<SequenceVariant>
      sequenceVariantAnnotationIndexReader;

  @BeforeEach
  void setUp() {
    sequenceVariantAnnotationIndexReader =
        new SequenceVariantAnnotationIndexReader<>(entryReader, indexReader);
  }

  @AfterEach
  void tearDown() {
    sequenceVariantAnnotationIndexReader.close();
  }

  @Test
  void read() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> indexDispatcher =
        mock(SequenceVariantAnnotationIndexDispatcher.class);

    when(entryReader.read(partitionKey)).thenReturn(memDecoder);
    when(indexReader.readFrom(memDecoder)).thenReturn(indexDispatcher);

    assertEquals(indexDispatcher, sequenceVariantAnnotationIndexReader.read(partitionKey));
  }

  @Test
  void readNotExists() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    assertNull(sequenceVariantAnnotationIndexReader.read(partitionKey));
  }

  @Test
  void readInto() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    MemoryDecoder memDecoder = mock(MemoryDecoder.class);
    when(entryReader.read(partitionKey)).thenReturn(memDecoder);

    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    assertTrue(sequenceVariantAnnotationIndexReader.readInto(partitionKey, index));
    verify(indexReader).readInto(memDecoder, index);
  }

  @Test
  void readIntoNotExists() {
    PartitionKey partitionKey = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    boolean indexExists = sequenceVariantAnnotationIndexReader.readInto(partitionKey, index);
    assertAll(() -> assertFalse(indexExists), () -> verifyNoInteractions(indexReader));
  }
}
