package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import java.util.EnumSet;
import java.util.List;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationData.Source;
import org.molgenis.vipannotate.util.Quantized16UnitIntervalDoublePrimitive;
import org.molgenis.vipannotate.util.ReusableBatchIterator;
import org.molgenis.vipannotate.util.SizedIterator;

// TODO perf: recycle memory buffers
public class GnomAdShortVariantAnnotationDataSetEncoder {
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

  public MemoryBuffer encodeAf(SizedIterator<Double> afIt) {
    return encodeQuantized16UnitIntervalDoublePrimitive(afIt);
  }

  public MemoryBuffer encodeFaf95(SizedIterator<Double> faf95It) {
    return encodeQuantized16UnitIntervalDoublePrimitive(faf95It);
  }

  public MemoryBuffer encodeFaf99(SizedIterator<Double> faf99It) {
    return encodeQuantized16UnitIntervalDoublePrimitive(faf99It);
  }

  public MemoryBuffer encodeHn(SizedIterator<Integer> hnIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(hnIt.size() * 8);
    hnIt.forEachRemaining(memoryBuffer::writeInt32);
    return memoryBuffer;
  }

  public MemoryBuffer encodeFilters(
      SizedIterator<EnumSet<GnomAdShortVariantAnnotationData.Filter>> filtersIt) {
    int nrAnnotationsPerByte = 2;
    int nrAnnotationBytes = Math.ceilDivExact(filtersIt.size(), nrAnnotationsPerByte);

    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(nrAnnotationBytes);
    for (ReusableBatchIterator<EnumSet<GnomAdShortVariantAnnotationData.Filter>> filtersBatchIt =
            new ReusableBatchIterator<>(filtersIt, nrAnnotationsPerByte);
        filtersBatchIt.hasNext(); ) {
      int encodedFiltersBatch = encodeFiltersBatch(filtersBatchIt.next());
      memoryBuffer.writeByte(encodedFiltersBatch);
    }

    return memoryBuffer;
  }

  private int encodeFiltersBatch(
      List<EnumSet<GnomAdShortVariantAnnotationData.Filter>> annotationList) {
    int nrBitsPerAnnotation = 4;

    int encodedFiltersBatch = 0;
    for (int i = 0, annotationListSize = annotationList.size(); i < annotationListSize; i++) {
      EnumSet<GnomAdShortVariantAnnotationData.Filter> filters = annotationList.get(i);
      if (!filters.isEmpty()) {
        if (filters.contains(GnomAdShortVariantAnnotationData.Filter.AC0)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation));
        }
        if (filters.contains(GnomAdShortVariantAnnotationData.Filter.AS_VQSR)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation) + 1);
        }
        if (filters.contains(GnomAdShortVariantAnnotationData.Filter.INBREEDING_COEFF)) {
          encodedFiltersBatch |= 1 << (((annotationListSize - 1 - i) * nrBitsPerAnnotation) + 2);
        }
      }
    }
    return encodedFiltersBatch;
  }

  public MemoryBuffer encodeCov(SizedIterator<Double> covIt) {
    return encodeQuantized16UnitIntervalDoublePrimitive(covIt);
  }

  public MemoryBuffer encodeQuantized16UnitIntervalDoublePrimitive(SizedIterator<Double> doubleIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(doubleIt.size() * 8);
    doubleIt.forEachRemaining(
        aDouble ->
            memoryBuffer.writeInt16(Quantized16UnitIntervalDoublePrimitive.toShort(aDouble)));
    return memoryBuffer;
  }
}
