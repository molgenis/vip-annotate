package org.molgenis.vipannotate.annotation;

import static java.util.Collections.emptyList;
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
import org.molgenis.vipannotate.util.IndexRange;

@ExtendWith(MockitoExtension.class)
class SequenceVariantAnnotationDbTest {
  @Mock private PartitionResolver partitionResolver;
  @Mock private AnnotationIndexReader<SequenceVariant> annotationIndexReader;
  @Mock private AnnotationDatasetReader<Annotation> annotationDatasetReader;
  private SequenceVariantAnnotationDb<SequenceVariant, Annotation> sequenceVariantAnnotationDb;

  @BeforeEach
  void setUp() {
    sequenceVariantAnnotationDb =
        new SequenceVariantAnnotationDb<>(
            partitionResolver, annotationIndexReader, annotationDatasetReader, _ -> true);
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
    AnnotationIndex<SequenceVariant> annotationIndex0 = mock(AnnotationIndex.class);
    IndexRange indexRange0 = mock(IndexRange.class);
    @SuppressWarnings("unchecked")
    AnnotationDataset<Annotation> annotationDataset0 = mock(AnnotationDataset.class);

    Annotation annotation0 = mock(Annotation.class);
    List<Annotation> annotations = new ArrayList<>();
    when(annotationIndex0.findIndexes(sequenceVariant0)).thenReturn(indexRange0);
    doAnswer(
            invocation -> {
              List<Annotation> annotationsArg = invocation.getArgument(1);
              annotationsArg.add(annotation0);
              return null;
            })
        .when(annotationDataset0)
        .findByIndexes(indexRange0, annotations);
    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    when(annotationIndexReader.read(partitionKey0)).thenReturn(annotationIndex0);
    when(annotationDatasetReader.read(partitionKey0)).thenReturn(annotationDataset0);

    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0, annotations);
    assertEquals(List.of(annotation0), annotations);
  }

  @Test
  void findAnnotationsIndexMiss() {
    SequenceVariant sequenceVariant0 = mock(SequenceVariant.class);
    PartitionKey partitionKey0 = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    AnnotationIndex<SequenceVariant> annotationIndex0 = mock(AnnotationIndex.class);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    when(annotationIndexReader.read(partitionKey0)).thenReturn(annotationIndex0);

    List<Annotation> annotations = new ArrayList<>();
    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0, annotations);
    verifyNoInteractions(annotationDatasetReader);
  }

  @Test
  void findAnnotationsSecondCallSamePartition() {
    SequenceVariant sequenceVariant0 = mock(SequenceVariant.class);
    SequenceVariant sequenceVariant1 = mock(SequenceVariant.class);
    PartitionKey partitionKey0 = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    AnnotationIndex<SequenceVariant> annotationIndex = mock(AnnotationIndex.class);
    IndexRange indexRange0 = mock(IndexRange.class);
    IndexRange indexRange1 = mock(IndexRange.class);
    @SuppressWarnings("unchecked")
    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);
    @SuppressWarnings("unchecked")
    List<Annotation> annotationList0 = mock(List.class);
    @SuppressWarnings("unchecked")
    List<Annotation> annotationList1 = mock(List.class);

    when(annotationIndex.findIndexes(sequenceVariant0)).thenReturn(indexRange0);
    when(annotationIndex.findIndexes(sequenceVariant1)).thenReturn(indexRange1);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    when(partitionResolver.resolvePartitionKey(sequenceVariant1)).thenReturn(partitionKey0);
    when(annotationIndexReader.read(partitionKey0)).thenReturn(annotationIndex);
    when(annotationDatasetReader.read(partitionKey0)).thenReturn(annotationDataset);

    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0, annotationList0);
    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant1, annotationList1);

    assertAll(
        () -> verify(annotationDataset).findByIndexes(indexRange0, annotationList0),
        () -> verify(annotationDataset).findByIndexes(indexRange1, annotationList1),
        () -> verify(annotationIndexReader, times(1)).read(partitionKey0),
        () -> verify(annotationDatasetReader, times(1)).read(partitionKey0));
  }

  @Test
  void findAnnotationsSecondCallOtherPartition() {
    SequenceVariant sequenceVariant0 = mock(SequenceVariant.class);
    SequenceVariant sequenceVariant1 = mock(SequenceVariant.class);
    PartitionKey partitionKey0 = mock(PartitionKey.class);
    PartitionKey partitionKey1 = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    AnnotationIndex<SequenceVariant> annotationIndex = mock(AnnotationIndex.class);

    when(partitionResolver.resolvePartitionKey(sequenceVariant0)).thenReturn(partitionKey0);
    when(partitionResolver.resolvePartitionKey(sequenceVariant1)).thenReturn(partitionKey1);
    when(annotationIndexReader.read(partitionKey0)).thenReturn(annotationIndex);

    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant0);
    sequenceVariantAnnotationDb.findAnnotations(sequenceVariant1);

    assertAll(
        () -> verify(annotationIndexReader, times(1)).read(partitionKey0),
        () -> verify(annotationIndexReader, times(1)).readInto(partitionKey1, annotationIndex));
  }

  @Test
  void findAnnotationsCanNotAnnotate() {
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    assertEquals(
        emptyList(),
        new SequenceVariantAnnotationDb<>(
                partitionResolver, annotationIndexReader, annotationDatasetReader, _ -> false)
            .findAnnotations(sequenceVariant));
  }
}
