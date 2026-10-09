package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationDbTest {
  @Mock private PartitionResolver partitionResolver;
  @Mock private SequenceVariantAnnotationIndexReader<SequenceVariant> annotationIndexReader;
  @Mock private AnnotationDatasetDecoder<Annotation> annotationDatasetReader;
  @Mock private Pool<Annotation> annotationPool;
  private SequenceVariantAnnotationDb<SequenceVariant, Annotation> sequenceVariantAnnotationDb;

  @BeforeEach
  void setUp() {
    sequenceVariantAnnotationDb =
        new SequenceVariantAnnotationDb<>(
            partitionResolver, annotationIndexReader, annotationDatasetReader, annotationPool);
  }

  @AfterEach
  void tearDown() {
    sequenceVariantAnnotationDb.close();
  }

  @Test
  void findAnnotations() {
    SequenceVariant sequenceVariant0 = mock(SequenceVariant.class);
    PartitionKey partitionKey0 = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> annotationIndex0 =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    when(sequenceVariant0.getStart()).thenReturn(123);
    @SuppressWarnings("unchecked")
    AnnotationDataset<Annotation> annotationDataset0 = mock(AnnotationDataset.class);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    PositionEncoding positionEncoding = mock(PositionEncoding.class);
    when(partitionResolver.resolvePosition(partitionKey0, 123)).thenReturn(positionEncoding);

    Annotation annotation0 = mock(CompositeAnnotation.class);
    List<Annotation> annotations = new ArrayList<>();
    when(annotationIndex0.findIndexesInto(eq(sequenceVariant0), eq(positionEncoding), any()))
        .thenReturn(true);
    doAnswer(
            invocation -> {
              List<Annotation> annotationsArg = invocation.getArgument(1);
              annotationsArg.add(annotation0);
              return null;
            })
        .when(annotationDataset0)
        .findByIndexesInto(any(), eq(annotations), any());
    when(annotationIndexReader.read(partitionKey0)).thenReturn(annotationIndex0);
    when(annotationDatasetReader.decode(partitionKey0)).thenReturn(annotationDataset0);

    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0, annotations);
    assertEquals(List.of(annotation0), annotations);
  }

  @Test
  void findAnnotationsIndexMiss() {
    SequenceVariant sequenceVariant0 = mock(SequenceVariant.class);
    when(sequenceVariant0.getStart()).thenReturn(123);
    PartitionKey partitionKey0 = mock(PartitionKey.class);
    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    PositionEncoding positionEncoding = mock(PositionEncoding.class);
    when(partitionResolver.resolvePosition(partitionKey0, 123)).thenReturn(positionEncoding);

    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> annotationIndex0 =
        mock(SequenceVariantAnnotationIndexDispatcher.class);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    when(annotationIndexReader.read(partitionKey0)).thenReturn(annotationIndex0);

    List<Annotation> annotations = new ArrayList<>();
    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0, annotations);
    verifyNoInteractions(annotationDatasetReader);
  }

  @Test
  void findAnnotationsSecondCallSamePartition() {
    PartitionKey partitionKey = mock(PartitionKey.class);

    SequenceVariant sequenceVariant0 = mock(SequenceVariant.class);
    when(sequenceVariant0.getStart()).thenReturn(123);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey);
    PositionEncoding positionEncoding0 = mock(PositionEncoding.class);
    when(partitionResolver.resolvePosition(partitionKey, 123)).thenReturn(positionEncoding0);

    SequenceVariant sequenceVariant1 = mock(SequenceVariant.class);
    when(sequenceVariant1.getStart()).thenReturn(234);
    when(partitionResolver.resolvePartitionKey(sequenceVariant1)).thenReturn(partitionKey);
    PositionEncoding positionEncoding1 = mock(PositionEncoding.class);
    when(partitionResolver.resolvePosition(partitionKey, 234)).thenReturn(positionEncoding1);

    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> annotationIndex =
        mock(SequenceVariantAnnotationIndexDispatcher.class);
    @SuppressWarnings("unchecked")
    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);
    @SuppressWarnings("unchecked")
    List<Annotation> annotationList0 = mock(List.class);
    @SuppressWarnings("unchecked")
    List<Annotation> annotationList1 = mock(List.class);

    when(annotationIndex.findIndexesInto(eq(sequenceVariant0), eq(positionEncoding0), any()))
        .thenReturn(true);
    when(annotationIndex.findIndexesInto(eq(sequenceVariant1), eq(positionEncoding1), any()))
        .thenReturn(true);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey);
    when(partitionResolver.resolvePartitionKey(sequenceVariant1)).thenReturn(partitionKey);
    when(annotationIndexReader.read(partitionKey)).thenReturn(annotationIndex);
    when(annotationDatasetReader.decode(partitionKey)).thenReturn(annotationDataset);

    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0, annotationList0);
    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant1, annotationList1);

    assertAll(
        () -> verify(annotationDataset).findByIndexesInto(any(), eq(annotationList0), any()),
        () -> verify(annotationDataset).findByIndexesInto(any(), eq(annotationList1), any()),
        () -> verify(annotationIndexReader, times(1)).read(partitionKey),
        () -> verify(annotationDatasetReader, times(1)).decode(partitionKey));
  }

  @Test
  void findAnnotationsSecondCallOtherPartition() {
    SequenceVariant sequenceVariant0 = mock(SequenceVariant.class);
    SequenceVariant sequenceVariant1 = mock(SequenceVariant.class);
    PartitionKey partitionKey0 = mock(PartitionKey.class);
    PartitionKey partitionKey1 = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    SequenceVariantAnnotationIndexDispatcher<SequenceVariant> annotationIndex =
        mock(SequenceVariantAnnotationIndexDispatcher.class);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    when(partitionResolver.resolvePartitionKey(sequenceVariant1)).thenReturn(partitionKey1);
    when(annotationIndexReader.read(partitionKey0)).thenReturn(annotationIndex);

    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0);
    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant1);

    assertAll(
        () -> verify(annotationIndexReader, times(1)).read(partitionKey0),
        () -> verify(annotationIndexReader, times(1)).readInto(partitionKey1, annotationIndex));
  }
}
