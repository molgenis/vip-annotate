package org.molgenis.vipannotate.annotation.gnomad;

import java.util.EnumSet;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotationData.Source;
import org.molgenis.vipannotate.util.Quantizer;

public class GnomAdAnnotationDatasetDecoder {
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

  public Double decodeAf(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDouble(memoryBuffer, afIndex);
  }

  public double decodeFaf95(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }

  public double decodeFaf99(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }

  public int decodeHn(MemoryBuffer memoryBuffer, int index) {
    return memoryBuffer.getInt32(index * 4);
  }

  private double decodeQuantized16UnitIntervalDoublePrimitive(
      MemoryBuffer memoryBuffer, int afIndex) {
    short value = memoryBuffer.getInt16(afIndex * Short.BYTES);
    int quantizedValue = Short.toUnsignedInt(value);
    return Quantizer.dequantize(quantizedValue, 0, (1 << Short.SIZE) - 1, 0d, 1d);
  }

  private Double decodeQuantized16UnitIntervalDouble(MemoryBuffer memoryBuffer, int afIndex) {
    short value = memoryBuffer.getInt16(afIndex * Short.BYTES);
    int quantizedValue = Short.toUnsignedInt(value);
    return quantizedValue != 0
        ? Quantizer.dequantize(quantizedValue, 1, (1 << Short.SIZE) - 1, 0d, 1d)
        : null;
  }

  // TODO perf: predefine all possible enum sets instead of creating new ones
  public EnumSet<GnomAdAnnotationData.Filter> decodeFilters(
      MemoryBuffer memoryBuffer, int sourceIndex) {
    int nrAnnotationsPerByte = 2;
    int nrBitsPerAnnotation = 4;

    int encodedFiltersBatch = memoryBuffer.getByte(sourceIndex / nrAnnotationsPerByte);
    int encodedFilters =
        (encodedFiltersBatch >> (((sourceIndex + 1) % nrAnnotationsPerByte) * nrBitsPerAnnotation))
            & 0b00_00_11_11;
    EnumSet<GnomAdAnnotationData.Filter> filters =
        EnumSet.noneOf(GnomAdAnnotationData.Filter.class);
    if (encodedFilters != 0) {
      if ((encodedFilters & 1) != 0) {
        filters.add(GnomAdAnnotationData.Filter.AC0);
      }
      if ((encodedFilters & (1 << 1)) != 0) {
        filters.add(GnomAdAnnotationData.Filter.AS_VQSR);
      }
      if ((encodedFilters & (1 << 2)) != 0) {
        filters.add(GnomAdAnnotationData.Filter.INBREEDING_COEFF);
      }
    }
    return filters;
  }

  public double decodeCov(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }
}
