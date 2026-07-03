package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

/**
 * parsed database-build command-line arguments
 *
 * @param inputRecipe input recipe.
 * @param outputDir output database directory.
 * @param force whether to overwrite the output database if it exists.
 */
public record DbBuildArgs(Path inputRecipe, @Nullable Path outputDir, @Nullable Boolean force) {}
