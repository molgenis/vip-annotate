package org.molgenis.vcf.annotate.db.chrpos.ncer;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.chrpos.ContigPosAnnotationDb;
import org.molgenis.vcf.annotate.db.chrpos.ZipCompressionContext;
import org.molgenis.vcf.annotate.db.chrpos.ncer.NcERIterator.NcERFeature;
import org.molgenis.vcf.annotate.db.effect.model.FuryFactory;
import org.molgenis.vcf.annotate.util.ContigUtils;

public class NcERAnnotationDbWriter {
  private String currentContig;
  private Integer currentPartitionId;

  public void create(
      NcERIterator ncERIterator,
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    reset();

    short[] encodedScores =
        new short[(int) (Math.pow(2, ContigPosAnnotationDb.NR_PARTITION_ID_BITS))];

    while (ncERIterator.hasNext()) {
      NcERFeature ncERFeature = ncERIterator.next();

      // FIXME liftover ncER score file instead of performing liftover during annotation db creation
      FuryFactory.Chromosome chromosome = ContigUtils.map(ncERFeature.chr());
      if (chromosome == null) {
        throw new RuntimeException(
            "cannot map contig '%s' to reference genome contig".formatted(ncERFeature.chr()));
      }
      String contig = chromosome.getId();
      int start = ncERFeature.start() + 1; // 0-based --> 1-based
      int end = ncERFeature.end() + 1; // 0-based --> 1-based
      double perc = ncERFeature.perc();
      short encodedPerc = NcERCodec.encode(perc);

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
      ZipCompressionContext zipCompressionContext,
      ZipArchiveOutputStream zipArchiveOutputStream) {
    MemoryBuffer memoryBuffer =
        MemoryBuffer.newHeapBuffer(
            encodedScores.length * NcERAnnotationDecoder.NR_ANNOTATION_BYTES);
    // TODO check if gives same result, but faster?
    // memoryBuffer.writePrimitiveArray(encodedScores, 0, encodedScores.length * 2);
    for (short encodedScore : encodedScores) {
      memoryBuffer.writeInt16(encodedScore);
    }
    byte[] uncompressedByteArray = memoryBuffer.getHeapMemory();

    String zipArchiveEntryName =
        contig
            + "/"
            + ContigPosAnnotationDb.ID
            + "/"
            + NcERAnnotationDecoder.ANNOTATION_ID
            + "/"
            + partitionId
            + ".zst";
    zipCompressionContext.writeData(
        zipArchiveEntryName, uncompressedByteArray, zipArchiveOutputStream);
  }

  private void reset() {
    currentContig = null;
    currentPartitionId = null;
  }
}
