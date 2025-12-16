package org.molgenis.vipannotate.util;

import java.nio.file.Files;
import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

/**
 * Output file
 *
 * @param path file path or <code>null</code> for stdout output
 */
public record Output(@Nullable Path path) {
  public Output {
    if (path != null && Files.exists(path) && !Files.isRegularFile(path)) {
      throw new IllegalArgumentException("'%s' is not a file".formatted(path));
    }
  }
}
