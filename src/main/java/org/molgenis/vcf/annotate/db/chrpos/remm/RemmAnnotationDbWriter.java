package org.molgenis.vcf.annotate.db.chrpos.remm;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.chrpos.ContigPosAnnotationDb;
import org.molgenis.vcf.annotate.db.chrpos.ZipCompressionContext;
import org.molgenis.vcf.annotate.db.chrpos.remm.RemmIterator.RemmFeature;
import org.molgenis.vcf.annotate.db.effect.model.FuryFactory;
import org.molgenis.vcf.annotate.util.ContigUtils;

/** same as v1 but with 8-bit instead of 16-bit quantized scores */
public class RemmAnnotationDbWriter {
  private String currentContig;
  private Integer currentPartitionId;

  public void create(
      RemmIterator remmIterator,
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    reset();

    byte[] encodedScores = new byte[1048576]; // partition size: 2 ^ 20

    while (remmIterator.hasNext()) {
      RemmFeature remmFeature = remmIterator.next();

      // FIXME liftover ncER score file instead of performing liftover during annotation db creation
      FuryFactory.Chromosome chromosome = ContigUtils.map(remmFeature.chr());
      if (chromosome == null) {
        throw new RuntimeException(
            "cannot map contig '%s' to reference genome contig".formatted(remmFeature.chr()));
      }
      String contig = chromosome.getId();
      int pos = remmFeature.start(); // 1-based
      double score = remmFeature.score();
      byte encodedScore = RemmCodec.encode(score);

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
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(encodedScores.length);
    // TODO check if gives same result, but faster?
    // memoryBuffer.writePrimitiveArray(encodedScores, 0, encodedScores.length);
    for (byte encodedScore : encodedScores) {
      memoryBuffer.writeByte(encodedScore);
    }

    String zipArchiveEntryName =
        contig
            + "/"
            + ContigPosAnnotationDb.ID
            + "/"
            + RemmAnnotationDecoder.ANNOTATION_ID
            + "/"
            + partitionId
            + ".zst";
    byte[] uncompressedByteArray = memoryBuffer.getHeapMemory();
    zipCompressionContext.writeData(
        zipArchiveEntryName, uncompressedByteArray, zipArchiveOutputStream);
  }

  private void reset() {
    currentContig = null;
    currentPartitionId = null;
  }
}
