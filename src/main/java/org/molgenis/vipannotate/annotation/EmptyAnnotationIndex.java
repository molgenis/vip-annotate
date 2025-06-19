package org.molgenis.vipannotate.annotation;

final class EmptyAnnotationIndex implements AnnotationIndex {
  private static final EmptyAnnotationIndex INSTANCE = new EmptyAnnotationIndex();

  private EmptyAnnotationIndex() {}

  public static EmptyAnnotationIndex getInstance() {
    return INSTANCE;
  }

  @Override
  public int findIndex(SequenceVariant variant) {
    return -1;
  }
}
