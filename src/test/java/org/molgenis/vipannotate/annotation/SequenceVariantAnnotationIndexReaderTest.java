package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// FIXME
// @SuppressWarnings({"unchecked"})
// @ExtendWith(MockitoExtension.class)
// class SequenceVariantAnnotationIndexReaderTest {
//  @Mock private AnnotationBlobReader annotationBlobReader;
//  @Mock private MemoryBufferReader<AnnotationIndex<SequenceVariant>> indexReader;
//
//  private SequenceVariantAnnotationIndexReader<SequenceVariant>
//      sequenceVariantAnnotationIndexReader;
//
//  @BeforeEach
//  void setUp() {
//    sequenceVariantAnnotationIndexReader =
//        new SequenceVariantAnnotationIndexReader<>(annotationBlobReader, indexReader);
//  }
//
//  @AfterEach
//  void tearDown() {
//    sequenceVariantAnnotationIndexReader.close();
//  }
//
//  @Test
//  void read() {
//    PartitionKey partitionKey = mock(PartitionKey.class);
//    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
//    BinaryReader binaryReader = mock(BinaryReader.class);
//    when(binaryReader.unwrap()).thenReturn(memoryBuffer);
//    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> indexDispatcher =
//        mock(SequenceVariantAnnotationIndexDispatcher.class);
//
//    when(annotationBlobReader.read(partitionKey)).thenReturn(binaryReader);
//    when(indexReader.readFrom(memoryBuffer)).thenReturn(indexDispatcher);
//
//    assertEquals(indexDispatcher, sequenceVariantAnnotationIndexReader.read(partitionKey));
//  }
//
//  @Test
//  void readNotExists() {
//    PartitionKey partitionKey = mock(PartitionKey.class);
//    assertNull(sequenceVariantAnnotationIndexReader.read(partitionKey));
//  }
//
//  @Test
//  void readInto() {
//    PartitionKey partitionKey = mock(PartitionKey.class);
//    MemoryBuffer memoryBuffer = mock(MemoryBuffer.class);
//    BinaryReader binaryReader = mock(BinaryReader.class);
//    when(binaryReader.unwrap()).thenReturn(memoryBuffer);
//    when(annotationBlobReader.read(partitionKey)).thenReturn(binaryReader);
//
//    @SuppressWarnings("unchecked")
//    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
//        mock(SequenceVariantAnnotationIndexDispatcher.class);
//    assertTrue(sequenceVariantAnnotationIndexReader.readInto(partitionKey, index));
//    verify(indexReader).readInto(memoryBuffer, index);
//  }
//
//  @Test
//  void readIntoNotExists() {
//    PartitionKey partitionKey = mock(PartitionKey.class);
//    @SuppressWarnings("unchecked")
//    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> index =
//        mock(SequenceVariantAnnotationIndexDispatcher.class);
//    boolean indexExists = sequenceVariantAnnotationIndexReader.readInto(partitionKey, index);
//    assertAll(() -> assertFalse(indexExists), () -> verifyNoInteractions(indexReader));
//  }
// }
