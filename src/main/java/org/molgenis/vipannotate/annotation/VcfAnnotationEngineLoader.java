package org.molgenis.vipannotate.annotation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VcfAnnotationEngineLoader {
  private final VcfAnnotationModuleLoader moduleLoader;

  public VcfAnnotationEngine load(Path annotationsDir) {
    VcfAnnotationEngine annotationEngine = new VcfAnnotationEngine();
    try (Stream<Path> paths = Files.list(annotationsDir)) {
      paths
          .filter(path -> path.toString().endsWith(".vdb"))
          .map(moduleLoader::load)
          .forEach(annotationEngine::registerModule);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return annotationEngine;
  }

  public static VcfAnnotationEngineLoader create() {
    VcfAnnotationModuleLoader moduleLoader = VcfAnnotationModuleLoader.create();
    return new VcfAnnotationEngineLoader(moduleLoader);
  }
}
