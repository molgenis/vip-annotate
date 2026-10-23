package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class PerElementAnnotationDatasetDecoder<T extends Annotation>
    implements AnnotationDatasetDecoder<T> {
  private final AnnotationDecoder<T> annotationDecoder;

  @Override
  public AnnotationDataset<T> decode(MemoryDecoderProvider decoderProvider) {
    MemoryDecoder memDecoder = decoderProvider.get();
    if (memDecoder == null) {
      return EmptyAnnotationDataset.getInstance();
    }

    return new PerElementAnnotationDataset<>(annotationDecoder, memDecoder);
  }
}
