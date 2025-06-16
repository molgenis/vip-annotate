package org.molgenis.vipannotate.annotation.gnomad;

import java.util.EnumSet;
import java.util.List;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotationData.Source;
import org.molgenis.vipannotate.util.*;

// TODO perf: recycle memory buffers
public class GnomAdAnnotationDatasetEncoder {
  public MemoryBuffer encodeSources(SizedIterator<Source> sourceIt) {
    int nrAnnotationsPerByte = 4;
    int nrAnnotationBytes = Math.ceilDivExact(sourceIt.size(), nrAnnotationsPerByte);

    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(nrAnnotationBytes);
    for (ReusableBatchIterator<Source> sourceBatchIt =
            new ReusableBatchIterator<>(sourceIt, nrAnnotationsPerByte);
        sourceBatchIt.hasNext(); ) {
      int encodedSourceBatch = encodeSourceBatch(sourceBatchIt.next());
      memoryBuffer.writeByte(encodedSourceBatch);
    }

    return memoryBuffer;
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
   * @param afIt iterator element can be <code>null</code>
   */
  public MemoryBuffer encodeAf(SizedIterator<Double> afIt) {
    return encodeQuantized16UnitIntervalDouble(afIt);
  }

  /**
   * @param faf95It iterator element must not be <code>null</code>
   */
  public MemoryBuffer encodeFaf95(SizedIterator<Double> faf95It) {
    return encodeQuantized16UnitIntervalDoublePrimitive(faf95It);
  }

  /**
   * @param faf99It iterator element must not be <code>null</code>
   */
  public MemoryBuffer encodeFaf99(SizedIterator<Double> faf99It) {
    return encodeQuantized16UnitIntervalDoublePrimitive(faf99It);
  }

  /**
   * @param hnIt iterator element must not be <code>null</code>
   */
  public MemoryBuffer encodeHn(SizedIterator<Integer> hnIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(hnIt.size() * Integer.BYTES);
    hnIt.forEachRemaining(memoryBuffer::writeInt32);
    return memoryBuffer;
  }

  public MemoryBuffer encodeFilters(SizedIterator<EnumSet<GnomAdAnnotationData.Filter>> filtersIt) {
    int nrAnnotationsPerByte = 2;
    int nrAnnotationBytes = Math.ceilDivExact(filtersIt.size(), nrAnnotationsPerByte);

    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(nrAnnotationBytes);
    for (ReusableBatchIterator<EnumSet<GnomAdAnnotationData.Filter>> filtersBatchIt =
            new ReusableBatchIterator<>(filtersIt, nrAnnotationsPerByte);
        filtersBatchIt.hasNext(); ) {
      int encodedFiltersBatch = encodeFiltersBatch(filtersBatchIt.next());
      memoryBuffer.writeByte(encodedFiltersBatch);
    }

    return memoryBuffer;
  }

  private int encodeFiltersBatch(List<EnumSet<GnomAdAnnotationData.Filter>> annotationList) {
    int nrBitsPerAnnotation = 4;

    int encodedFiltersBatch = 0;
    for (int i = 0, annotationListSize = annotationList.size(); i < annotationListSize; i++) {
      EnumSet<GnomAdAnnotationData.Filter> filters = annotationList.get(i);
      if (!filters.isEmpty()) {
        if (filters.contains(GnomAdAnnotationData.Filter.AC0)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation));
        }
        if (filters.contains(GnomAdAnnotationData.Filter.AS_VQSR)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation) + 1);
        }
        if (filters.contains(GnomAdAnnotationData.Filter.INBREEDING_COEFF)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation) + 2);
        }
      }
    }
    return encodedFiltersBatch;
  }

  /**
   * @param covIt iterator element must not be <code>null</code>
   */
  public MemoryBuffer encodeCov(SizedIterator<Double> covIt) {
    return encodeQuantized16UnitIntervalDoublePrimitive(covIt);
  }

  private MemoryBuffer encodeQuantized16UnitIntervalDoublePrimitive(
      SizedIterator<Double> doubleIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(doubleIt.size() * Short.BYTES);
    doubleIt.forEachRemaining(
        value -> {
          short encodedValue = Encoder.encodeUnitIntervalDoublePrimitiveAsShort(value);
          memoryBuffer.writeInt16(encodedValue);
        });
    return memoryBuffer;
  }

  private MemoryBuffer encodeQuantized16UnitIntervalDouble(SizedIterator<Double> doubleIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(doubleIt.size() * Short.BYTES);
    doubleIt.forEachRemaining(
        value -> {
          short encodedValue = Encoder.encodeUnitIntervalDoubleAsShort(value);
          memoryBuffer.writeInt16(encodedValue);
        });
    return memoryBuffer;
  }
}
