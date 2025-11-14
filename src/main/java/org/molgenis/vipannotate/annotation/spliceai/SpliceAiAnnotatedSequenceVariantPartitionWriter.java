package org.molgenis.vipannotate.annotation.spliceai;

import java.util.*;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
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
    PartitionKey partitionKey = partition.key();
    List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedFeatures =
        partition.annotatedIntervals();

    Map<Integer, Integer> ncbiGeneIdToLocalGeneIdMap = new LinkedHashMap<>();
    int localIndex = 0;
    for (AnnotatedSequenceVariant<SpliceAiAnnotation> annotatedFeature : annotatedFeatures) {
      int ncbiGeneId = annotatedFeature.getAnnotation().ncbiGeneId();
      if (!ncbiGeneIdToLocalGeneIdMap.containsKey(ncbiGeneId)) {
        ncbiGeneIdToLocalGeneIdMap.put(ncbiGeneId, localIndex++);
      }
    }
    int[] ncbiGeneIds =
        ncbiGeneIdToLocalGeneIdMap.keySet().stream().mapToInt(Integer::intValue).toArray();

    //noinspection DataFlowIssue
    writeGeneIndex(partitionKey, ncbiGeneIds);
    writeGene(
        partitionKey,
        annotatedFeatures,
        SpliceAiAnnotation::ncbiGeneId,
        ncbiGeneIdToLocalGeneIdMap);

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

  private void writeGeneIndex(PartitionKey partitionKey, int[] geneIndexes) {
    MemoryBuffer memBuffer = MemoryBuffer.wrap(new int[geneIndexes.length]);
    for (int geneIndex : geneIndexes) {
      memBuffer.putInt(geneIndex);
    }
    memBuffer.flip();
    binaryPartitionWriter.write(partitionKey, "gene_idx", memBuffer);
  }

  private void writeGene(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Integer> geneIdFunction,
      Map<Integer, Integer> ncbiGeneIdToLocalGeneIdMap) {
    MemoryBuffer memBuffer =
        spliceAiAnnotationDatasetEncoder.encodeGeneId(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant ->
                        ncbiGeneIdToLocalGeneIdMap.get(
                            geneIdFunction.apply(annotatedVariant.getAnnotation()))),
                annotatedVariants.size()));
    memBuffer.flip();
    binaryPartitionWriter.write(partitionKey, "gene_ref", memBuffer);
  }

  private void writeScore(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Double> scoreFunction,
      String dataId) {
    MemoryBuffer memBuffer =
        spliceAiAnnotationDatasetEncoder.encodeScore(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> scoreFunction.apply(annotatedVariant.getAnnotation())),
                annotatedVariants.size()));
    memBuffer.flip();
    binaryPartitionWriter.write(partitionKey, dataId, memBuffer);
  }

  private void writePos(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Byte> posFunction,
      String dataId) {
    MemoryBuffer memBuffer =
        spliceAiAnnotationDatasetEncoder.encodePos(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotatedVariants.iterator(),
                    annotatedVariant -> posFunction.apply(annotatedVariant.getAnnotation())),
                annotatedVariants.size()));
    memBuffer.flip();
    binaryPartitionWriter.write(partitionKey, dataId, memBuffer);
  }
}
