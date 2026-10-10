package org.molgenis.vipannotate.annotation;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vdb.BinaryPartitionWriter;
import org.molgenis.vipannotate.format.vdb.Compression;
import org.molgenis.vipannotate.format.vdb.IoMode;
import org.molgenis.vipannotate.serialization.BinaryWriter;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.ScratchMemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

// TODO writing ref index as previously used in SpliceAiAnnotatedSequenceVariantPartitionWriter
// @Override
// public void write(
//        Partition<SequenceVariant, SpliceAiAnnotation,
// AnnotatedSequenceVariant<SpliceAiAnnotation>>
//                partition) {
//  PartitionKey partitionKey = partition.key();
//  List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedFeatures =
//          partition.annotatedIntervals();
//
//
//  Map<Integer, Integer> ncbiGeneIdToLocalGeneIdMap = new LinkedHashMap<>();
//  int localIndex = 0;
//  for (AnnotatedSequenceVariant<SpliceAiAnnotation> annotatedFeature : annotatedFeatures) {
//    int ncbiGeneId = annotatedFeature.getAnnotation().ncbiGeneId();
//    if (!ncbiGeneIdToLocalGeneIdMap.containsKey(ncbiGeneId)) {
//      ncbiGeneIdToLocalGeneIdMap.put(ncbiGeneId, localIndex++);
//    }
//  }
//  int[] ncbiGeneIds =
//          ncbiGeneIdToLocalGeneIdMap.keySet().stream().mapToInt(Integer::intValue).toArray();
//
//  //noinspection DataFlowIssue
//  writeGeneIndex(partitionKey, ncbiGeneIds);
//  writeGene(
//          partitionKey,
//          annotatedFeatures,
//          SpliceAiAnnotation::ncbiGeneId,
//          ncbiGeneIdToLocalGeneIdMap);
// }
//
// private void writeGeneIndex(PartitionKey partitionKey, int[] geneIndexes) {
//  MemoryBuffer memBuffer =
//          getHeapBackedScratchBuffer(Math.toIntExact((long) geneIndexes.length * Integer.BYTES));
//  for (int geneIndex : geneIndexes) {
//    memBuffer.putInt(geneIndex);
//  }
//  binaryPartitionWriter.write(
//          "gene_idx", Compression.PLAIN, IoMode.BUFFERED, memBuffer, partitionKey);
// }
//
// private void writeGene(
//        PartitionKey partitionKey,
//        List<AnnotatedSequenceVariant<SpliceAiAnnotation>> annotatedVariants,
//        Function<SpliceAiAnnotation, Integer> geneIdFunction,
//        Map<Integer, Integer> ncbiGeneIdToLocalGeneIdMap) {
//  // prepare
//  SizedIterator<@Nullable Integer> geneIdIt =
//          new SizedIterator<>(
//                  new TransformingIterator<>(
//                          annotatedVariants.iterator(),
//                          annotatedVariant ->
//                                  ncbiGeneIdToLocalGeneIdMap.get(
//
// geneIdFunction.apply(annotatedVariant.getAnnotation()))),
//                  annotatedVariants.size());
//
//  // encode
//  long encodedSize = spliceAiAnnotationDatasetEncoder.calcEncodedGeneIdSize(geneIdIt);
//  MemoryBuffer memBuffer = getHeapBackedScratchBuffer(encodedSize);
//  spliceAiAnnotationDatasetEncoder.encodeGeneId(geneIdIt, memBuffer);
//
//  // write
//  binaryPartitionWriter.write(
//          "gene_ref", Compression.PLAIN, IoMode.BUFFERED, memBuffer, partitionKey);
// }
@RequiredArgsConstructor
public class AnnotatedSequenceVariantPartitionWriter<
        T extends SequenceVariant,
        U extends Annotation, // type of sequence variant annotation
        V extends Annotation, // type of sequence variant annotation part to write to partition
        W extends AnnotatedInterval<T, U>>
    implements AnnotatedIntervalPartitionWriter<T, U, W> {
  private final ScratchMemoryBuffer scratchBuffer = new ScratchMemoryBuffer();

  private final String annotationDataId;
  private final AnnotationDatasetEncoder<V> annotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;
  private final Function<W, V> annotationExtractor;

  @Override
  public void write(Partition<T, U, W> partition) {
    // prepare
    List<W> annotatedVariants = partition.annotatedIntervals();
    SizedIterator<V> annotationIt =
        new SizedIterator<>(
            new TransformingIterator<>(annotatedVariants.iterator(), annotationExtractor),
            annotatedVariants.size());

    // encode
    long encodedAnnotationByteSize =
        annotationDatasetEncoder.getEncodedSizeInBytes(annotationIt.getSize());
    MemoryBuffer memBuffer = scratchBuffer.get(encodedAnnotationByteSize);
    annotationDatasetEncoder.encode(annotationIt, BinaryWriter.fixed(memBuffer));

    // write
    binaryPartitionWriter.write(
        annotationDataId, Compression.ZSTD, IoMode.DIRECT, memBuffer, partition.key());
  }

  @Override
  public void close() {
    ClosableUtils.close(scratchBuffer);
  }
}
