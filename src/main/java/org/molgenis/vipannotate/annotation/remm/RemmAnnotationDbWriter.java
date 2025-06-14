package org.molgenis.vipannotate.annotation.remm;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.FastaIndex;
import org.molgenis.vipannotate.zip.ZipCompressionContextOther;

// TODO refactor: use generic AnnotationDbWriter, remove this class
/** same as v1 but with 8-bit instead of 16-bit quantized scores */
public class RemmAnnotationDbWriter {
  private String currentContig;
  private Integer currentPartitionId;

  public void create(
      RemmIterator remmIterator,
      FastaIndex fastaIndex,
      ZipCompressionContextOther zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    reset();

    byte[] encodedScores = new byte[1048576]; // partition size: 2 ^ 20

    while (remmIterator.hasNext()) {
      RemmTsvRecord remmTsvRecord = remmIterator.next();

      String contig = remmTsvRecord.chr();
      if (!fastaIndex.notContainsReferenceSequence(contig)) {
        throw new RuntimeException(
            "Fasta index does not contain reference sequence %s".formatted(contig));
      }

      int pos = remmTsvRecord.start(); // 1-based
      double score = remmTsvRecord.score();
      byte encodedScore = RemmAnnotationDataCodec.encode(score);

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
        encodedScores = new byte[1048576]; // partition size: 2 ^ 20
        currentContig = contig;
        currentPartitionId = partitionId;
      }

      int binIndex = pos >> 20;
      int relPos = pos - (binIndex << 20);
      encodedScores[relPos] = encodedScore;
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
      byte[] encodedScores,
      ZipCompressionContextOther zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(encodedScores.length);
    // TODO check if gives same result, but faster?
    // memoryBuffer.writePrimitiveArray(encodedScores, 0, encodedScores.length);
    for (byte encodedScore : encodedScores) {
      memoryBuffer.writeByte(encodedScore);
    }

    String zipArchiveEntryName = contig + "/" + partitionId + "/scores.zst";
    byte[] uncompressedByteArray = memoryBuffer.getHeapMemory();
    zipCompressionContext.writeData(
        zipArchiveEntryName, uncompressedByteArray, zipArchiveOutputStream);
  }

  private void reset() {
    currentContig = null;
    currentPartitionId = null;
  }
}
