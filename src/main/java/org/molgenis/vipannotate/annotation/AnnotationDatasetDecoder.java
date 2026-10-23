package org.molgenis.vipannotate.annotation;

/// decodes annotations
public interface AnnotationDatasetDecoder<T extends Annotation> {
  /// decodes annotations from provided memory decoders
  ///
  /// @param decoderProvider provides memory decoders for specific dataset components
  AnnotationDataset<T> decode(MemoryDecoderProvider decoderProvider);
}
