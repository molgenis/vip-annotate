package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import java.util.EnumSet;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationData.Source;
import org.molgenis.vipannotate.db.Quantized16UnitIntervalDoublePrimitive;

public class GnomAdShortVariantAnnotationDatasetDecoder {
  public Source decodeSource(MemoryBuffer memoryBuffer, int sourceIndex) {
    int nrAnnotationsPerByte = 4;
    int nrBitsPerAnnotation = 2;

    int encodedSourceBatch = memoryBuffer.getByte(sourceIndex / nrAnnotationsPerByte);

    int encodedSource =
        (encodedSourceBatch >> ((sourceIndex % nrAnnotationsPerByte) * nrBitsPerAnnotation))
            & 0b00_00_00_11;
    return switch (encodedSource) {
      case 0 -> Source.GENOMES;
      case 1 -> Source.EXOMES;
      case 2 -> Source.TOTAL;
      default -> throw new IllegalStateException("Unexpected value: " + encodedSource);
    };
  }

  public double decodeAf(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }

  public double decodeFaf95(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }

  public double decodeFaf99(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }

  public int decodeHn(MemoryBuffer memoryBuffer, int index) {
    memoryBuffer.readerIndex(index * 4);
    return memoryBuffer.readInt32();
  }

  private double decodeQuantized16UnitIntervalDoublePrimitive(
      MemoryBuffer memoryBuffer, int afIndex) {
    memoryBuffer.readerIndex(afIndex * 2);
    short encodedDouble = memoryBuffer.readInt16();
    return Quantized16UnitIntervalDoublePrimitive.toDouble(encodedDouble);
  }

  // TODO perf: predefine all possible enum sets instead of creating new ones
  public EnumSet<GnomAdShortVariantAnnotationData.Filter> decodeFilters(
      MemoryBuffer memoryBuffer, int sourceIndex) {
    int nrAnnotationsPerByte = 2;
    int nrBitsPerAnnotation = 4;
    memoryBuffer.readerIndex(sourceIndex / nrAnnotationsPerByte);

    int encodedFiltersBatch = memoryBuffer.readByte();
    int encodedFilters =
        (encodedFiltersBatch >> (((sourceIndex + 1) % nrAnnotationsPerByte) * nrBitsPerAnnotation))
            & 0b00_00_11_11;
    EnumSet<GnomAdShortVariantAnnotationData.Filter> filters =
        EnumSet.noneOf(GnomAdShortVariantAnnotationData.Filter.class);
    if (encodedFilters != 0) {
      if ((encodedFilters & 1) != 0) {
        filters.add(GnomAdShortVariantAnnotationData.Filter.AC0);
      }
      if ((encodedFilters & (1 << 1)) != 0) {
        filters.add(GnomAdShortVariantAnnotationData.Filter.AS_VQSR);
      }
      if ((encodedFilters & (1 << 2)) != 0) {
        filters.add(GnomAdShortVariantAnnotationData.Filter.INBREEDING_COEFF);
      }
    }
    return filters;
  }

  public double decodeCov(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }
}
