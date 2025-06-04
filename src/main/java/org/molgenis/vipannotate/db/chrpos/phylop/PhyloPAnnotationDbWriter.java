package org.molgenis.vipannotate.db.chrpos.phylop;

import static java.util.Objects.requireNonNull;

import java.util.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.chrpos.ContigPosAnnotation;
import org.molgenis.vipannotate.db.chrpos.ContigPosEncoder;
import org.molgenis.vipannotate.db.chrpos.ZipCompressionContext;
import org.molgenis.vipannotate.util.FastaIndex;

public class PhyloPAnnotationDbWriter {
  private final ContigPosEncoder contigPosEncoder;

  private String currentContig;
  private Integer currentPartitionId;

  public PhyloPAnnotationDbWriter() {
    this(new ContigPosEncoder());
  }

  /** package-private constructor for unit testing */
  PhyloPAnnotationDbWriter(ContigPosEncoder contigPosEncoder) {
    this.contigPosEncoder = requireNonNull(contigPosEncoder);
  }

  public void create(
      Iterator<ContigPosAnnotation> chrPosAnnotationIterator,
      FastaIndex fastaIndex,
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    reset();

    short[] encodedScores = new short[1048576]; // partition size: 2 ^20

    while (chrPosAnnotationIterator.hasNext()) {
      ContigPosAnnotation chrPosAnnotation = chrPosAnnotationIterator.next();

      String contig = chrPosAnnotation.contig();
      if (!fastaIndex.containsReferenceSequence(contig)) {
        continue;
        //        throw new RuntimeException("Fasta index does not contain reference sequence
        // %s".formatted(contig));
      }

      int pos = chrPosAnnotation.pos();
      String score = chrPosAnnotation.score();

      // encode
      double valUnsigned = Double.parseDouble(score) - (-11.726);
      short encodedScore = (short) ((valUnsigned * 1000) + 1); // reserve 0 for missing values

      if (currentContig == null) {
        currentContig = contig;
      }

      int partitionId = contigPosEncoder.getPartitionId(chrPosAnnotation);
      if (currentPartitionId == null) {
        currentPartitionId = partitionId;
      }

      if (partitionId != currentPartitionId || !contig.equals(currentContig)) {
        // TODO do not write full array for last partition but only up to contig length
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
      short[] encodedScores,
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(encodedScores.length * 2);
    // TODO check if gives same result, but faster?
    // memoryBuffer.writePrimitiveArray(encodedScores, 0, encodedScores.length * 2);
    for (short encodedScore : encodedScores) {
      memoryBuffer.writeInt16(encodedScore);
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
