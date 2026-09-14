package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAnnotatedIntervalPartitionWriter<
        T extends Interval, V extends AnnotatedInterval<T, CompositeAnnotation>>
    implements AnnotatedIntervalPartitionWriter<T, CompositeAnnotation, V> {

  private final List<AnnotatedIntervalPartitionWriter<T, CompositeAnnotation, V>> partitionWriters;

  @Override
  public void write(Partition<T, CompositeAnnotation, V> partition) {
    for (AnnotatedIntervalPartitionWriter<T, CompositeAnnotation, V> partitionWriter :
        partitionWriters) {
      partitionWriter.write(partition);
    }
  }

  @Override
  public void close() {
    for (AnnotatedIntervalPartitionWriter<T, CompositeAnnotation, V> partitionWriter :
        partitionWriters) {
      partitionWriter.close();
    }
  }
}
