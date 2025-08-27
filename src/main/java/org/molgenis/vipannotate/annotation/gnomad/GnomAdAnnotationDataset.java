package org.molgenis.vipannotate.annotation.gnomad;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDataset;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Filter;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source;

@RequiredArgsConstructor
public class GnomAdAnnotationDataset implements AnnotationDataset<GnomAdAnnotation> {
  private final GnomAdAnnotationDatasetDecoder annotationDataDecoder;
  private final MemoryBuffer srcMemoryBuffer;
  private final MemoryBuffer afMemoryBuffer;
  private final MemoryBuffer faf95MemoryBuffer;
  private final MemoryBuffer faf99MemoryBuffer;
  private final MemoryBuffer hnMemoryBuffer;
  private final MemoryBuffer filtersMemoryBuffer;
  private final MemoryBuffer covMemoryBuffer;

  @Override
  public GnomAdAnnotation findByIndex(int index) {
    Source source = annotationDataDecoder.decodeSource(srcMemoryBuffer, index);
    Double af = annotationDataDecoder.decodeAf(afMemoryBuffer, index);
    double faf95 = annotationDataDecoder.decodeFaf95(faf95MemoryBuffer, index);
    double faf99 = annotationDataDecoder.decodeFaf99(faf99MemoryBuffer, index);
    int hn = annotationDataDecoder.decodeHn(hnMemoryBuffer, index);
    EnumSet<Filter> filters = annotationDataDecoder.decodeFilters(filtersMemoryBuffer, index);
    double cov = annotationDataDecoder.decodeCov(covMemoryBuffer, index);

    return new GnomAdAnnotation(source, af, faf95, faf99, hn, filters, cov);
  }
}
