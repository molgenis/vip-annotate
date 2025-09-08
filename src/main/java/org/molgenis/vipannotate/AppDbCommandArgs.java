package org.molgenis.vipannotate;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

/** {@link org.molgenis.vipannotate.AppDb} command-line arguments shared between commands */
public record AppDbCommandArgs(
    Path inputFile,
    Path faiFile,
    Path outputFile,
    @Nullable String regionsStr,
    @Nullable Boolean force,
    @Nullable Boolean debugMode) {}
