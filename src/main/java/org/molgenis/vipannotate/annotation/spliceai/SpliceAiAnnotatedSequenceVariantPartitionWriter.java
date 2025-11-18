package org.molgenis.vipannotate.annotation.spliceai;

import java.util.*;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Numbers;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class SpliceAiAnnotatedSequenceVariantPartitionWriter
    implements AnnotatedIntervalPartitionWriter<
        SequenceVariant, SpliceAiAnnotation, AnnotatedSequenceVariant<SpliceAiAnnotation>> {
  private final SpliceAiAnnotationDatasetEncoder spliceAiAnnotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;
  @Nullable private MemoryBuffer scratchBuffer;

  @Override
  public void write(
      Partition<SequenceVariant, SpliceAiAnnotation, AnnotatedSequenceVariant<SpliceAiAnnotation>>
          partition) {
    PartitionKey partitionKey = partition.key();
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
  }

  private void writeGeneIndex(PartitionKey partitionKey, int[] geneIndexes) {
    MemoryBuffer memBuffer =
        getHeapBackedScratchBuffer(Math.toIntExact((long) geneIndexes.length * Integer.BYTES));
    for (int geneIndex : geneIndexes) {
      memBuffer.putInt(geneIndex);
    }
    binaryPartitionWriter.write(
        partitionKey, "gene_idx", Compression.PLAIN, IoMode.BUFFERED, memBuffer);
  }

  private void writeGene(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Integer> geneIdFunction,
      Map<Integer, Integer> ncbiGeneIdToLocalGeneIdMap) {
    // prepare
    SizedIterator<@Nullable Integer> geneIdIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant ->
                    ncbiGeneIdToLocalGeneIdMap.get(
                        geneIdFunction.apply(annotatedVariant.getAnnotation()))),
            annotatedVariants.size());

    // encode
    long encodedSize = spliceAiAnnotationDatasetEncoder.calcEncodedGeneIdSize(geneIdIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    spliceAiAnnotationDatasetEncoder.encodeGeneId(geneIdIt, memBuffer);

    // write
    binaryPartitionWriter.write(
        partitionKey, "gene_ref", Compression.PLAIN, IoMode.BUFFERED, memBuffer);
  }

  private void writeScore(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Double> scoreFunction,
      String dataId) {
    // prepare
    SizedIterator<Double> scoreIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> scoreFunction.apply(annotatedVariant.getAnnotation())),
            annotatedVariants.size());

    // encode
    long encodedSize = spliceAiAnnotationDatasetEncoder.calcEncodedScoreSize(scoreIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    spliceAiAnnotationDatasetEncoder.encodeScore(scoreIt, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, dataId, Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private void writePos(
      PartitionKey partitionKey,
      List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
      Function<SpliceAiAnnotation, Byte> posFunction,
      String dataId) {
    // prepare
    SizedIterator<@Nullable Byte> posIt =
        new SizedIterator<>(
            new TransformingIterator<>(
                annotatedVariants.iterator(),
                annotatedVariant -> posFunction.apply(annotatedVariant.getAnnotation())),
            annotatedVariants.size());

    // encode
    long encodedSize = spliceAiAnnotationDatasetEncoder.calcEncodedPosSize(posIt);
    MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
    spliceAiAnnotationDatasetEncoder.encodePos(posIt, memBuffer);

    // write
    binaryPartitionWriter.write(partitionKey, dataId, Compression.ZSTD, IoMode.DIRECT, memBuffer);
  }

  private MemoryBuffer getHeapBackedScratchBuffer(long minCapacity) {
    if (scratchBuffer == null) {
      scratchBuffer = MemoryBuffer.wrap(new byte[Math.toIntExact(minCapacity)]);
    } else {
      if (minCapacity > scratchBuffer.getCapacity()) {
        // ensureCapacity does not support heap backed buffers, create a new one
        scratchBuffer.close();
        scratchBuffer =
            MemoryBuffer.wrap(new byte[Math.toIntExact(Numbers.nextPowerOf2(minCapacity))]);
      } else {
        scratchBuffer.clear();
      }
    }
    return scratchBuffer;
  }

  @Override
  public void close() {
    if (scratchBuffer != null) {
      scratchBuffer.close();
    }
  }
}
