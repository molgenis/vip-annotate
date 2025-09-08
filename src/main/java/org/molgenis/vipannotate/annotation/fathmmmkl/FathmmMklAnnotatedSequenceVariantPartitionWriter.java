package org.molgenis.vipannotate.annotation.fathmmmkl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotatedIntervalPartitionWriter;
import org.molgenis.vipannotate.annotation.AnnotatedSequenceVariant;
import org.molgenis.vipannotate.annotation.BinaryPartitionWriter;
import org.molgenis.vipannotate.annotation.Partition;
import org.molgenis.vipannotate.annotation.SequenceVariant;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class FathmmMklAnnotatedSequenceVariantPartitionWriter
    implements AnnotatedIntervalPartitionWriter<
        SequenceVariant, FathmmMklAnnotation, AnnotatedSequenceVariant<FathmmMklAnnotation>> {
  private final FathmmMklAnnotationDatasetEncoder annotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;

  @Override
  public void write(
      Partition<SequenceVariant, FathmmMklAnnotation, AnnotatedSequenceVariant<FathmmMklAnnotation>>
          partition) {
    Partition.Key partitionKey = partition.key();
    List<AnnotatedSequenceVariant<FathmmMklAnnotation>> annotatedFeatures =
        partition.annotatedIntervals();

    writeScore(partitionKey, annotatedFeatures);
  }

  private void writeScore(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<FathmmMklAnnotation>> annotatedVariants) {
    MemoryBuffer memoryBuffer =
        annotationDatasetEncoder.encodeScores(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> annotatedVariant.getAnnotation().score()),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, "score", memoryBuffer);
  }
}
