package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.Input;

/**
 * parsed database-build command-line arguments
 *
 * @param input input.
 * @param inputRecipe input recipe.
 * @param outputDir output database directory.
 * @param force whether to overwrite the output database if it exists.
 */
public record DbBuildArgs(
    Input input, Path inputRecipe, @Nullable Path outputDir, @Nullable Boolean force) {}
