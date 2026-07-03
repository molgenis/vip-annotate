package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Output;

@RequiredArgsConstructor
public class VcfAnnotatorFactory {
  private final VcfAnnotationEngineLoader annotationEngineLoader;

  public VcfAnnotator create(
      Input inputVcf, Path annotationsDir, Output outputVcf, @Nullable VcfType outputVcfType) {
    VcfParser vcfParser = VcfParserFactory.create(inputVcf);
    VcfAnnotationEngine annotationEngine = annotationEngineLoader.load(annotationsDir);
    VcfWriter vcfWriter = VcfWriterFactory.create(outputVcf, outputVcfType);
    return new VcfAnnotator(vcfParser, annotationEngine, vcfWriter);
  }

  public static VcfAnnotatorFactory create() {
    return new VcfAnnotatorFactory(VcfAnnotationEngineLoader.create());
  }
}
