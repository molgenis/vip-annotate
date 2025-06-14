package org.molgenis.vipannotate.annotation.ncer;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.zip.ZipCompressionContextOther;

// TODO refactor: use generic AnnotationDbWriter, remove this class
public class NcERAnnotationDbWriter {
  private String currentContig;
  private Integer currentPartitionId;

  public void create(
      NcERIterator ncERIterator,
      FastaIndex fastaIndex,
      ZipCompressionContextOther zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    reset();

    short[] encodedScores = new short[(int) (Math.pow(2, 20))];

    while (ncERIterator.hasNext()) {
      NcERBedFeature ncERBedFeature = ncERIterator.next();

      String contig = ncERBedFeature.chr();
      if (!fastaIndex.notContainsReferenceSequence(contig)) {
        throw new RuntimeException(
            "Fasta index does not contain reference sequence %s".formatted(contig));
      }

      int start = ncERBedFeature.start() + 1; // 0-based --> 1-based
      int end = ncERBedFeature.end() + 1; // 0-based --> 1-based
      double perc = ncERBedFeature.perc();
      short encodedPerc = NcERAnnotationDataCodec.encodeScore(perc);

      for (int pos = start; pos < end; pos++) {
        if (currentContig == null) {
          currentContig = contig;
        }

        int partitionId = pos >> 20;
        if (currentPartitionId == null) {
          currentPartitionId = partitionId;
        }

        if (partitionId != currentPartitionId || !contig.equals(currentContig)) {
          // FIXME do not write full array for last partition but only up to contig length
          write(
              currentContig,
              currentPartitionId,
              encodedScores,
              zipCompressionContext,
              zipArchiveOutputStream);

          // reset
          encodedScores = new short[1048576]; // partition size: 2 ^ 20
          currentContig = contig;
          currentPartitionId = partitionId;
        }

        int binIndex = pos >> 20;
        int relPos = pos - (binIndex << 20);
        encodedScores[relPos] = encodedPerc;
      }
    }

    // TODO check if there are cases when there is no remainder to write
    write(
        currentContig,
        currentPartitionId,
        encodedScores,
        zipCompressionContext,
        zipArchiveOutputStream);
  }

  private void write(
      String contig,
      int partitionId,
      short[] encodedScores,
      ZipCompressionContextOther zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    MemoryBuffer memoryBuffer =
        MemoryBuffer.newHeapBuffer(
            encodedScores.length * NcERAnnotationDataCodec.NR_ANNOTATION_BYTES);
    // TODO check if gives same result, but faster?
    // memoryBuffer.writePrimitiveArray(encodedScores, 0, encodedScores.length * 2);
    for (short encodedScore : encodedScores) {
      memoryBuffer.writeInt16(encodedScore);
    }
    byte[] uncompressedByteArray = memoryBuffer.getHeapMemory();

    String zipArchiveEntryName = contig + "/" + partitionId + "/scores.zst";
    zipCompressionContext.writeData(
        zipArchiveEntryName, uncompressedByteArray, zipArchiveOutputStream);
  }

  private void reset() {
    currentContig = null;
    currentPartitionId = null;
  }
}
