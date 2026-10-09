package org.molgenis.vipannotate.annotation;
// FIXME
// import static java.util.Collections.emptyList;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.util.ArrayList;
// import java.util.List;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class PositionAnnotationDbTest {
//  @Mock private PartitionResolver partitionResolver;
//  @Mock private AnnotationDatasetDecoder<Annotation> annotationDatasetReader;
//  @Mock private Pool<Annotation> annotationPool;
//  private PositionAnnotationDb<Annotation> positionAnnotationDb;
//
//  @BeforeEach
//  void setUp() {
//    positionAnnotationDb =
//        new PositionAnnotationDb<>(
//            partitionResolver, annotationDatasetReader, _ -> true, annotationPool);
//  }
//
//  @AfterEach
//  void tearDown() {
//    positionAnnotationDb.close();
//  }
//
//  @Test
//  void findAnnotations() {
//    Contig contig = mock(Contig.class);
//    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
//    when(sequenceVariant.getContig()).thenReturn(contig);
//    when(sequenceVariant.getStart()).thenReturn(123);
//    when(sequenceVariant.getRefLength()).thenReturn(3);
//    PartitionKey partitionKey = mock(PartitionKey.class);
//    @SuppressWarnings("unchecked")
//    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);
//    Annotation annotation0 = mock(CompositeAnnotation.class);
//    Annotation annotation1 = mock(CompositeAnnotation.class);
//
//    when(annotationPool.acquire()).thenReturn(annotation0).thenReturn(annotation1);
//    when(partitionResolver.resolvePartitionKey(contig, 123)).thenReturn(partitionKey);
//    when(partitionResolver.resolvePartitionKey(contig, 124)).thenReturn(partitionKey);
//    when(partitionResolver.resolvePartitionKey(contig, 125)).thenReturn(partitionKey);
//    when(partitionResolver.getPartitionPos(contig, 123)).thenReturn(456);
//    when(partitionResolver.getPartitionPos(contig, 124)).thenReturn(457);
//    when(partitionResolver.getPartitionPos(contig, 125)).thenReturn(458);
//    when(annotationDatasetReader.decode(partitionKey)).thenReturn(annotationDataset);
//
//    List<Annotation> annotationList = new ArrayList<>();
//    positionAnnotationDb.findAnnotations(sequenceVariant, annotationList);
//
//    assertAll(
//        () -> assertEquals(List.of(annotation0, annotation1), annotationList),
//        () -> verify(annotationDatasetReader, times(1)).decode(partitionKey),
//        () -> {
//          verify(annotationDataset).findByIndexInto(456, annotation0);
//          verify(annotationDataset).findByIndexInto(457, annotation1);
//        });
//  }
//
//  @Test
//  void findAnnotationsRefLengthOne() {
//    Contig contig = mock(Contig.class);
//    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
//    when(sequenceVariant.getContig()).thenReturn(contig);
//    when(sequenceVariant.getStart()).thenReturn(123);
//    when(sequenceVariant.getRefLength()).thenReturn(1);
//    PartitionKey partitionKey = mock(PartitionKey.class);
//    @SuppressWarnings("unchecked")
//    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);
//    Annotation annotation = mock(CompositeAnnotation.class);
//    when(annotationPool.acquire()).thenReturn(annotation);
//
//    when(partitionResolver.resolvePartitionKey(contig, 123)).thenReturn(partitionKey);
//    when(partitionResolver.getPartitionPos(contig, 123)).thenReturn(456);
//    when(annotationDatasetReader.decode(partitionKey)).thenReturn(annotationDataset);
//
//    assertAll(
//        () ->
//            assertEquals(
//                List.of(annotation), positionAnnotationDb.findAnnotations(sequenceVariant)),
//        () -> verify(annotationDataset).findByIndexInto(456, annotation));
//  }
//
//  @Test
//  void findAnnotationsRefLengthOneNull() {
//    Contig contig = mock(Contig.class);
//    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
//    when(sequenceVariant.getContig()).thenReturn(contig);
//    when(sequenceVariant.getStart()).thenReturn(123);
//    when(sequenceVariant.getRefLength()).thenReturn(1);
//    PartitionKey partitionKey = mock(PartitionKey.class);
//    @SuppressWarnings("unchecked")
//    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);
//    Annotation annotation = mock(CompositeAnnotation.class);
//    when(annotationPool.acquire()).thenReturn(annotation);
//
//    when(partitionResolver.resolvePartitionKey(contig, 123)).thenReturn(partitionKey);
//    when(partitionResolver.getPartitionPos(contig, 123)).thenReturn(456);
//    when(annotationDatasetReader.decode(partitionKey)).thenReturn(annotationDataset);
//
//    assertAll(
//        () -> assertEquals(emptyList(), positionAnnotationDb.findAnnotations(sequenceVariant)),
//        () -> verify(annotationDataset).findByIndexInto(456, annotation));
//  }
//
//  @Test
//  void findAnnotationsCanNotAnnotate() {
//    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
//    assertEquals(
//        emptyList(),
//        new PositionAnnotationDb<>(
//                partitionResolver, annotationDatasetReader, _ -> false, annotationPool)
//            .findAnnotations(sequenceVariant));
//  }
// }
