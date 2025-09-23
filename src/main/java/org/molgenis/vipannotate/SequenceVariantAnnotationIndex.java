package org.molgenis.vipannotate;

public class SequenceVariantAnnotationIndex implements AnnotationIndex<SequenceVariant> {
  private final SequenceVariantAnnotationIndexSmall sequenceVariantAnnotationIndexSmall;
  private final SequenceVariantAnnotationIndexBig sequenceVariantAnnotationIndexBig;

  public SequenceVariantAnnotationIndex(
      SequenceVariantAnnotationIndexSmall sequenceVariantAnnotationIndexSmall,
      SequenceVariantAnnotationIndexBig sequenceVariantAnnotationIndexBig) {
    this.sequenceVariantAnnotationIndexSmall = sequenceVariantAnnotationIndexSmall;
    this.sequenceVariantAnnotationIndexBig = sequenceVariantAnnotationIndexBig;
  }

  @Override
  public IndexRange findIndexes(SequenceVariant feature) {
    return null;
  }

  public SequenceVariantAnnotationIndexSmall getSequenceVariantAnnotationIndexSmall() {
    return sequenceVariantAnnotationIndexSmall;
  }

  public SequenceVariantAnnotationIndexBig getSequenceVariantAnnotationIndexBig() {
    return sequenceVariantAnnotationIndexBig;
  }
}
