package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.RankBitmap;
import org.molgenis.vipannotate.util.SizedIterator;

@RequiredArgsConstructor
public class NullableAnnotationDatasetEncoder<T extends Annotation>
    implements AnnotationDatasetEncoder<T> {
  private final AnnotationDatasetEncoder<T> annotationDataSetEncoder;

  @Override
  public void encodeInto(SizedIterator<T> annotationIt, MemoryEncoderProvider encoderProvider) {
    // FIXME cleanup
    MemoryBuffer nullIndexMemBuffer = MemoryBuffer.allocateHeap(1024);
    MemoryEncoder nullIndexEncoder = MemoryEncoder.autoGrowing(nullIndexMemBuffer);

    int nrAnnotations = annotationIt.getSize();
    RankBitmap.Builder bitmapBuilder = RankBitmap.builder(nrAnnotations);
    List<T> annotations = new ArrayList<>(nrAnnotations);

    for (int i = 0; i < nrAnnotations; i++) {
      T annotation = annotationIt.next();

      if (!annotation.isMissingValue()) {
        bitmapBuilder.set(i);
        annotations.add(annotation);
      }
    }
    bitmapBuilder.build().writeInto(nullIndexEncoder);

    MemoryBuffer datasetMemBuffer = MemoryBuffer.allocateHeap(1024);
    MemoryEncoder datasetEncoder = MemoryEncoder.autoGrowing(datasetMemBuffer);
    annotationDataSetEncoder.encodeInto(
        new SizedIterator<>(annotations.iterator(), annotations.size()), () -> datasetEncoder);

    MemoryEncoder memEncoder = encoderProvider.get();
    nullIndexMemBuffer.flip();
    memEncoder.putMemoryEncoder(nullIndexEncoder);
    datasetMemBuffer.flip();
    memEncoder.putMemoryEncoder(datasetEncoder);
  }
}
