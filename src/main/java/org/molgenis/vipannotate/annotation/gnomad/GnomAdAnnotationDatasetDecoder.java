package org.molgenis.vipannotate.annotation.gnomad;

import java.util.EnumSet;
import org.apache.fory.memory.MemoryBuffer;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source;
import org.molgenis.vipannotate.util.DoubleCodec;

public class GnomAdAnnotationDatasetDecoder {
  private final DoubleCodec doubleCodec;

  public GnomAdAnnotationDatasetDecoder() {
    this(new DoubleCodec());
  }

  GnomAdAnnotationDatasetDecoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

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

  public @Nullable Double decodeAf(MemoryBuffer memoryBuffer, int afIndex) {
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

  public EnumSet<GnomAdAnnotation.Filter> decodeFilters(
      MemoryBuffer memoryBuffer, int sourceIndex) {
    int nrAnnotationsPerByte = 2;
    int nrBitsPerAnnotation = 4;

    int encodedFiltersBatch = memoryBuffer.getByte(sourceIndex / nrAnnotationsPerByte);
    int encodedFilters =
        (encodedFiltersBatch >> (((sourceIndex + 1) % nrAnnotationsPerByte) * nrBitsPerAnnotation))
            & 0b00_00_11_11;
    EnumSet<GnomAdAnnotation.Filter> filters = EnumSet.noneOf(GnomAdAnnotation.Filter.class);
    if (encodedFilters != 0) {
      if ((encodedFilters & 1) != 0) {
        filters.add(GnomAdAnnotation.Filter.AC0);
      }
      if ((encodedFilters & (1 << 1)) != 0) {
        filters.add(GnomAdAnnotation.Filter.AS_VQSR);
      }
      if ((encodedFilters & (1 << 2)) != 0) {
        filters.add(GnomAdAnnotation.Filter.INBREEDING_COEFF);
      }
    }
    return filters;
  }

  public double decodeCov(MemoryBuffer memoryBuffer, int afIndex) {
    return decodeQuantized16UnitIntervalDoublePrimitive(memoryBuffer, afIndex);
  }

  private double decodeQuantized16UnitIntervalDoublePrimitive(
      MemoryBuffer memoryBuffer, int afIndex) {
    short value = memoryBuffer.getInt16(afIndex * Short.BYTES);
    return doubleCodec.decodeDoubleUnitIntervalPrimitiveFromShort(value);
  }

  private @Nullable Double decodeQuantized16UnitIntervalDouble(
      MemoryBuffer memoryBuffer, int afIndex) {
    short value = memoryBuffer.getInt16(afIndex * Short.BYTES);
    return doubleCodec.decodeDoubleUnitIntervalFromShort(value);
  }
}
