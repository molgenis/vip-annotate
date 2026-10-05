package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.def.VcfInputFormat;
import org.molgenis.vipannotate.annotation.spec.AnnotationsSpec;

@RequiredArgsConstructor
public class VcfAnnotatedFeatureReader implements AnnotatedFeatureReader {
  private final Path vcfInput;
  private final VcfInputFormat tsvInputFormat;
  private final AnnotationsSpec annotationSpecs;

  @Override
  public boolean hasNext() {
    throw new UnsupportedOperationException(); // FIXME implement
  }

  @Override
  public AnnotatedFeature<?, ?> next() {
    throw new UnsupportedOperationException(); // FIXME implement
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException(); // FIXME implement
  }
}
