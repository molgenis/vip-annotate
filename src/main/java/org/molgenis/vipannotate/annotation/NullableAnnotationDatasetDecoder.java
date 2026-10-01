package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.util.RankBitmap;

@RequiredArgsConstructor
public class NullableAnnotationDatasetDecoder<T extends Annotation>
    implements AnnotationDatasetDecoder<T> {
  private final AnnotationDatasetDecoder<T> datasetDecoder;

  @Override
  public AnnotationDataset<T> decode(MemoryDecoderProvider decoderProvider) {
    MemoryDecoder memDecoder = decoderProvider.get();
    if (memDecoder == null) {
      return EmptyAnnotationDataset.getInstance();
    }

    RankBitmap rankBitmap = RankBitmap.readFrom(memDecoder.getMemoryDecoder());
    MemoryDecoder datasetDecoder = memDecoder.getMemoryDecoder();
    return new NullableAnnotationDataset<>(
        rankBitmap, this.datasetDecoder.decode(() -> datasetDecoder));
  }
}
