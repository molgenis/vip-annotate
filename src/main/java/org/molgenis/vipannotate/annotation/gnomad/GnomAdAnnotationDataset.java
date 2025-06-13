package org.molgenis.vipannotate.annotation.gnomad;

import java.util.EnumSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDataset;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotationData.Filter;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotationData.Source;

@RequiredArgsConstructor
public class GnomAdAnnotationDataset implements AnnotationDataset<GnomAdAnnotationData> {
  @NonNull private final GnomAdAnnotationDatasetDecoder annotationDataDecoder;
  @NonNull private final MemoryBuffer srcMemoryBuffer;
  @NonNull private final MemoryBuffer afMemoryBuffer;
  @NonNull private final MemoryBuffer faf95MemoryBuffer;
  @NonNull private final MemoryBuffer faf99MemoryBuffer;
  @NonNull private final MemoryBuffer hnMemoryBuffer;
  @NonNull private final MemoryBuffer filtersMemoryBuffer;
  @NonNull private final MemoryBuffer covMemoryBuffer;

  @Override
  public GnomAdAnnotationData findById(int index) {
    Source source = annotationDataDecoder.decodeSource(srcMemoryBuffer, index);
    double af = annotationDataDecoder.decodeAf(afMemoryBuffer, index);
    double faf95 = annotationDataDecoder.decodeFaf95(faf95MemoryBuffer, index);
    double faf99 = annotationDataDecoder.decodeFaf99(faf99MemoryBuffer, index);
    int hn = annotationDataDecoder.decodeHn(hnMemoryBuffer, index);
    EnumSet<Filter> filters = annotationDataDecoder.decodeFilters(filtersMemoryBuffer, index);
    double cov = annotationDataDecoder.decodeCov(covMemoryBuffer, index);

    return new GnomAdAnnotationData(source, af, faf95, faf99, hn, filters, cov);
  }
}
