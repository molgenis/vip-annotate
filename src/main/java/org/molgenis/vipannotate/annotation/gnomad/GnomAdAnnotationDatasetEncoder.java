package org.molgenis.vipannotate.annotation.gnomad;

import static org.molgenis.vipannotate.util.Numbers.safeIntToByte;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.*;

public class GnomAdAnnotationDatasetEncoder {
  private static final int NR_SOURCE_ANNOTATIONS_PER_BYTE = 4;

  private final DoubleCodec doubleCodec;

  public GnomAdAnnotationDatasetEncoder() {
    this(new DoubleCodec());
  }

  GnomAdAnnotationDatasetEncoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public void encodeSources(SizedIterator<Source> sourceIt, MemoryBuffer memBuffer) {
    List<Source> reusableSourceList = new ArrayList<>(NR_SOURCE_ANNOTATIONS_PER_BYTE);
    PredicateBatchIterator<Source> sourceBatchIt =
        new PredicateBatchIterator<>(
            sourceIt,
            (batch, _) -> batch.size() < NR_SOURCE_ANNOTATIONS_PER_BYTE,
            reusableSourceList);
    sourceBatchIt.forEachRemaining(
        sourceBatch -> {
          int encodedSourceBatch = encodeSourceBatch(sourceBatch);
          memBuffer.putByteUnchecked((byte) encodedSourceBatch);
        });
  }

  public long calcEncodedSourcesSize(SizedIterator<Source> sourceIt) {
    int nrAnnotationBytes = Math.ceilDivExact(sourceIt.getSize(), NR_SOURCE_ANNOTATIONS_PER_BYTE);
    return nrAnnotationBytes * Byte.BYTES;
  }

  private int encodeSourceBatch(List<Source> annotationList) {
    int encodedSourceBatch = 0;
    for (int i = 0, annotationListSize = annotationList.size(); i < annotationListSize; i++) {
      int encodedSource = encodeSource(annotationList.get(i));
      encodedSourceBatch |= encodedSource << (i * 2);
    }
    return encodedSourceBatch;
  }

  private int encodeSource(Source source) {
    return switch (source) {
      case GENOMES -> 0;
      case EXOMES -> 1;
      case TOTAL -> 2;
    };
  }

  /**
   * Encode allele frequencies
   *
   * @param afIt iterator element can be <code>null</code>
   */
  public void encodeAf(SizedIterator<@Nullable Double> afIt, MemoryBuffer memBuffer) {
    encodeQuantized16UnitIntervalDouble(afIt, memBuffer);
  }

  public long calcEncodedAfSize(SizedIterator<Double> afIt) {
    return (long) afIt.getSize() * Short.BYTES;
  }

  /**
   * Encode filter allele frequency (95% confidence)
   *
   * @param faf95It iterator element must not be <code>null</code>
   */
  public void encodeFaf95(SizedIterator<Double> faf95It, MemoryBuffer memBuffer) {
    encodeQuantized16UnitIntervalDoublePrimitive(faf95It, memBuffer);
  }

  public long calcEncodedFaf95Size(SizedIterator<Double> faf95It) {
    return (long) faf95It.getSize() * Short.BYTES;
  }

  /**
   * Encode filter allele frequency (99% confidence)
   *
   * @param faf99It iterator element must not be <code>null</code>
   */
  public void encodeFaf99(SizedIterator<Double> faf99It, MemoryBuffer memBuffer) {
    encodeQuantized16UnitIntervalDoublePrimitive(faf99It, memBuffer);
  }

  public long calcEncodedFaf99Size(SizedIterator<Double> faf99It) {
    return (long) faf99It.getSize() * Short.BYTES;
  }

  /**
   * Encode number of homozygotes
   *
   * @param hnIt iterator element must not be <code>null</code>
   */
  public void encodeHn(SizedIterator<Integer> hnIt, MemoryBuffer memBuffer) {
    hnIt.forEachRemaining(memBuffer::putInt);
  }

  public long calcEncodedHnSize(SizedIterator<Integer> hnIt) {
    return (long) hnIt.getSize() * Integer.BYTES;
  }

  public void encodeFilters(
      SizedIterator<EnumSet<GnomAdAnnotation.Filter>> filtersIt, MemoryBuffer memBuffer) {
    int nrAnnotationsPerByte = 2;

    List<EnumSet<GnomAdAnnotation.Filter>> reusableSourceList =
        new ArrayList<>(nrAnnotationsPerByte);
    PredicateBatchIterator<EnumSet<GnomAdAnnotation.Filter>> filtersBatchIt =
        new PredicateBatchIterator<>(
            filtersIt, (batch, _) -> batch.size() < nrAnnotationsPerByte, reusableSourceList);
    filtersBatchIt.forEachRemaining(
        filtersBatch -> {
          int encodedFiltersBatch = encodeFiltersBatch(filtersBatch);
          memBuffer.putByteUnchecked(safeIntToByte(encodedFiltersBatch));
        });
  }

  public long calcEncodedFiltersSize(SizedIterator<EnumSet<GnomAdAnnotation.Filter>> filtersIt) {
    int nrAnnotationsPerByte = 2;
    int nrAnnotationBytes = Math.ceilDivExact(filtersIt.getSize(), nrAnnotationsPerByte);
    return (long) nrAnnotationBytes * Byte.BYTES;
  }

  private int encodeFiltersBatch(List<EnumSet<GnomAdAnnotation.Filter>> annotationList) {
    int nrBitsPerAnnotation = 4;

    int encodedFiltersBatch = 0;
    for (int i = 0, annotationListSize = annotationList.size(); i < annotationListSize; i++) {
      EnumSet<GnomAdAnnotation.Filter> filters = annotationList.get(i);
      if (!filters.isEmpty()) {
        if (filters.contains(GnomAdAnnotation.Filter.AC0)) {
          encodedFiltersBatch |= 1 << ((annotationListSize - 1 - i) * nrBitsPerAnnotation);
        }
        if (filters.contains(GnomAdAnnotation.Filter.AS_VQSR)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation) + 1);
        }
        if (filters.contains(GnomAdAnnotation.Filter.INBREEDING_COEFF)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation) + 2);
        }
      }
    }
    return encodedFiltersBatch;
  }

  /**
   * Encode coverage
   *
   * @param covIt iterator element must not be <code>null</code>
   */
  public void encodeCov(SizedIterator<Double> covIt, MemoryBuffer memBuffer) {
    encodeQuantized16UnitIntervalDoublePrimitive(covIt, memBuffer);
  }

  public long calcEncodedCovSize(SizedIterator<Double> covIt) {
    return (long) covIt.getSize() * Short.BYTES;
  }

  private void encodeQuantized16UnitIntervalDoublePrimitive(
      SizedIterator<Double> doubleIt, MemoryBuffer memBuffer) {
    doubleIt.forEachRemaining(
        doubleValue -> {
          short encodedValue = doubleCodec.encodeDoubleUnitIntervalPrimitiveAsShort(doubleValue);
          memBuffer.putShort(encodedValue);
        });
  }

  private void encodeQuantized16UnitIntervalDouble(
      SizedIterator<@Nullable Double> doubleIt, MemoryBuffer memBuffer) {
    doubleIt.forEachRemaining(
        doubleValue -> {
          short encodedValue = doubleCodec.encodeDoubleUnitIntervalAsShort(doubleValue);
          memBuffer.putShort(encodedValue);
        });
  }
}
