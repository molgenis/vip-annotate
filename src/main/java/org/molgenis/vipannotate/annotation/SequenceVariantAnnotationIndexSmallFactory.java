package org.molgenis.vipannotate.annotation;

public class SequenceVariantAnnotationIndexSmallFactory<T extends SequenceVariant> {
  private SequenceVariantAnnotationIndexSmallFactory() {}

  /** create a new empty index */
  public static <T extends SequenceVariant> SequenceVariantAnnotationIndexSmall<T> create() {
    // TODO reuse same encoder
    return new SequenceVariantAnnotationIndexSmall<>(
        new SequenceVariantEncoderSmall<>(), new int[0]);
  }
}
