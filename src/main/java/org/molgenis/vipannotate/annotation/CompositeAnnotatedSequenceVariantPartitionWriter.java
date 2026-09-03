package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAnnotatedSequenceVariantPartitionWriter<
        T extends SequenceVariant, V extends AnnotatedInterval<T, CompositeAnnotation>>
    implements AnnotatedIntervalPartitionWriter<T, CompositeAnnotation, V> {

  private final List<AnnotatedSequenceVariantPartitionWriter<T, CompositeAnnotation, Annotation, V>>
      partitionWriters;

  @Override
  public void write(Partition<T, CompositeAnnotation, V> partition) {
    for (AnnotatedSequenceVariantPartitionWriter<T, CompositeAnnotation, Annotation, V>
        partitionWriter : partitionWriters) {
      partitionWriter.write(partition);
    }
  }

  @Override
  public void close() {
    for (AnnotatedSequenceVariantPartitionWriter<T, CompositeAnnotation, Annotation, V>
        partitionWriter : partitionWriters) {
      partitionWriter.close();
    }
  }
}
