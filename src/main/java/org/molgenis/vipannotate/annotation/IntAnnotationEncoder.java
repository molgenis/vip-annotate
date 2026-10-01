package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryWriter;

@RequiredArgsConstructor
public class IntAnnotationEncoder implements AnnotationEncoder<IntAnnotation> {
  private final IntValueWriter intValueWriter;

  @Override
  public void encodeInto(IntAnnotation annotation, BinaryWriter binaryWriter) {
    intValueWriter.write(annotation.getValue(), binaryWriter);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }
}
