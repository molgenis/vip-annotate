package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.Input;

public record DbBuildSubCommandArgs(
    Input input, Path faiFile, Path output, @Nullable String regionsStr, @Nullable Boolean force) {}
