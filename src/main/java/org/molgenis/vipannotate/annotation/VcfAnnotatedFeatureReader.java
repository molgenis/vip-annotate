package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.resolved.ResolvedAnnotationSpecs;
import org.molgenis.vipannotate.annotation.spec.VcfInputFormat;

@RequiredArgsConstructor
public class VcfAnnotatedFeatureReader implements AnnotatedFeatureReader {
  private final Path vcfInput;
  private final VcfInputFormat tsvInputFormat;
  private final ResolvedAnnotationSpecs annotationSpecs;

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
