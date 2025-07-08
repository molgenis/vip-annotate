package org.molgenis.vipannotate.annotation.spliceai;

import java.util.List;
import java.util.function.Function;
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
public class SpliceAiAnnotatedSequenceVariantPartitionWriter
    implements AnnotatedIntervalPartitionWriter<
        SequenceVariant, SpliceAiAnnotation, AnnotatedSequenceVariant<SpliceAiAnnotation>> {
  private final SpliceAiAnnotationDatasetEncoder spliceAiAnnotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;

  @Override
  public void write(
      Partition<SequenceVariant, SpliceAiAnnotation, AnnotatedSequenceVariant<SpliceAiAnnotation>>
          partition) {
    Partition.Key partitionKey = partition.key();
    List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedFeatures =
        partition.annotatedIntervals();

    writeScore(
        partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaScoreAcceptorGain, "ds_ag");
    writeScore(
        partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaScoreAcceptorLoss, "ds_al");
    writeScore(partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaScoreDonorGain, "ds_dg");
    writeScore(partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaScoreDonorLoss, "ds_dl");
    writePos(
        partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaPositionAcceptorGain, "dp_ag");
    writePos(
        partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaPositionAcceptorLoss, "dp_al");
    writePos(partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaPositionDonorGain, "dp_dg");
    writePos(partitionKey, annotatedFeatures, SpliceAiAnnotation::deltaPositionDonorLoss, "dp_dl");
  }

  private void writeScore(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Double> scoreFunction,
      String dataId) {
    MemoryBuffer memoryBuffer =
        spliceAiAnnotationDatasetEncoder.encodeScore(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> scoreFunction.apply(annotatedVariant.getAnnotation())),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, dataId, memoryBuffer);
  }

  private void writePos(
      Partition.Key partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Byte> posFunction,
      String dataId) {
    MemoryBuffer memoryBuffer =
        spliceAiAnnotationDatasetEncoder.encodePos(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> posFunction.apply(annotatedVariant.getAnnotation())),
                annotatedVariants.size()));
    binaryPartitionWriter.write(partitionKey, dataId, memoryBuffer);
  }
}
