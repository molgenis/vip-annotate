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

@ExtendWith(MockitoExtension.class)
class PositionAnnotationDbTest {
  @Mock private PartitionResolver partitionResolver;
  @Mock private AnnotationDatasetReader<Annotation> annotationDatasetReader;
  private PositionAnnotationDb<Annotation> positionAnnotationDb;

  @BeforeEach
  void setUp() {
    positionAnnotationDb =
        new PositionAnnotationDb<>(partitionResolver, annotationDatasetReader, _ -> true);
  }

  @AfterEach
  void tearDown() {
    positionAnnotationDb.close();
  }

  @Test
  void findAnnotations() {
    Contig contig = mock(Contig.class);
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(sequenceVariant.getContig()).thenReturn(contig);
    when(sequenceVariant.getStart()).thenReturn(123);
    when(sequenceVariant.getRefLength()).thenReturn(3);
    PartitionKey partitionKey = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);
    Annotation annotation0 = mock(Annotation.class);
    Annotation annotation1 = mock(Annotation.class);

    when(partitionResolver.resolvePartitionKey(contig, 123)).thenReturn(partitionKey);
    when(partitionResolver.resolvePartitionKey(contig, 124)).thenReturn(partitionKey);
    when(partitionResolver.resolvePartitionKey(contig, 125)).thenReturn(partitionKey);
    when(partitionResolver.getPartitionPos(123)).thenReturn(456);
    when(partitionResolver.getPartitionPos(124)).thenReturn(457);
    when(partitionResolver.getPartitionPos(125)).thenReturn(458);
    when(annotationDatasetReader.read(partitionKey)).thenReturn(annotationDataset);

    when(annotationDataset.findByIndex(456)).thenReturn(annotation0);
    when(annotationDataset.findByIndex(457)).thenReturn(null);
    when(annotationDataset.findByIndex(458)).thenReturn(annotation1);

    List<Annotation> annotationList = new ArrayList<>();
    positionAnnotationDb.findAnnotations(sequenceVariant, annotationList);

    assertAll(
        () -> assertEquals(List.of(annotation0, annotation1), annotationList),
        () -> verify(annotationDatasetReader, times(1)).read(partitionKey));
  }

  @Test
  void findAnnotationsRefLengthOne() {
    Contig contig = mock(Contig.class);
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(sequenceVariant.getContig()).thenReturn(contig);
    when(sequenceVariant.getStart()).thenReturn(123);
    when(sequenceVariant.getRefLength()).thenReturn(1);
    PartitionKey partitionKey = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);
    Annotation annotation = mock(Annotation.class);

    when(partitionResolver.resolvePartitionKey(contig, 123)).thenReturn(partitionKey);
    when(partitionResolver.getPartitionPos(123)).thenReturn(456);
    when(annotationDatasetReader.read(partitionKey)).thenReturn(annotationDataset);
    when(annotationDataset.findByIndex(456)).thenReturn(annotation);

    assertAll(
        () ->
            assertEquals(
                List.of(annotation), positionAnnotationDb.findAnnotations(sequenceVariant)));
  }

  @Test
  void findAnnotationsRefLengthOneNull() {
    Contig contig = mock(Contig.class);
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    when(sequenceVariant.getContig()).thenReturn(contig);
    when(sequenceVariant.getStart()).thenReturn(123);
    when(sequenceVariant.getRefLength()).thenReturn(1);
    PartitionKey partitionKey = mock(PartitionKey.class);
    @SuppressWarnings("unchecked")
    AnnotationDataset<Annotation> annotationDataset = mock(AnnotationDataset.class);

    when(partitionResolver.resolvePartitionKey(contig, 123)).thenReturn(partitionKey);
    when(partitionResolver.getPartitionPos(123)).thenReturn(456);
    when(annotationDatasetReader.read(partitionKey)).thenReturn(annotationDataset);
    when(annotationDataset.findByIndex(456)).thenReturn(null);

    assertAll(
        () -> assertEquals(emptyList(), positionAnnotationDb.findAnnotations(sequenceVariant)));
  }

  @Test
  void findAnnotationsCanNotAnnotate() {
    SequenceVariant sequenceVariant = mock(SequenceVariant.class);
    assertEquals(
        emptyList(),
        new PositionAnnotationDb<>(partitionResolver, annotationDatasetReader, _ -> false)
            .findAnnotations(sequenceVariant));
  }
}
