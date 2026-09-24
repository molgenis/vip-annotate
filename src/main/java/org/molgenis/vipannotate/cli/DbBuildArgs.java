package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

/**
 * parsed database-build command-line arguments
 *
 * @param input input file path.
 * @param inputDef input definition file path.
 * @param outputDir output database directory.
 * @param force whether to overwrite the output database if it exists.
 */
public record DbBuildArgs(
    Path input, Path inputDef, @Nullable Path outputDir, @Nullable Boolean force) {}
