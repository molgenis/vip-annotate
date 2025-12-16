package org.molgenis.vipannotate.util;

import java.nio.file.Files;
import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

/**
 * Input file
 *
 * @param path file path or <code>null</code> for stdin input
 */
public record Input(@Nullable Path path) {
  public Input() {
    this(null);
  }

  public Input {
    if (path != null) {
      if (Files.notExists(path)) {
        throw new IllegalArgumentException("'%s' does not exist".formatted(path));
      }
      if (!Files.isRegularFile(path)) {
        throw new IllegalArgumentException("'%s' is not a file".formatted(path));
      }
    }
  }
}
