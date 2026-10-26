package org.molgenis.vipannotate.annotation;

import java.nio.file.Path;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VcfAnnotationEngineLoader {
  private final VcfAnnotationModuleLoader moduleLoader;

  public VcfAnnotationEngine load(List<Path> annotationFiles) {
    VcfAnnotationEngine annotationEngine = new VcfAnnotationEngine();
    annotationFiles.stream().map(moduleLoader::load).forEach(annotationEngine::registerModule);
    return annotationEngine;
  }

  public static VcfAnnotationEngineLoader create() {
    VcfAnnotationModuleLoader moduleLoader = VcfAnnotationModuleLoader.create();
    return new VcfAnnotationEngineLoader(moduleLoader);
  }
}
