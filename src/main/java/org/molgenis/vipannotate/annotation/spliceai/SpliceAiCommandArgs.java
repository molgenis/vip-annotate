package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

public record SpliceAiCommandArgs(
    Path inputFile,
    Path ncbiGeneFile,
    Path faiFile,
    Path outputFile,
    @Nullable String regionsStr,
    @Nullable Boolean force,
    @Nullable Boolean debugMode) {}
