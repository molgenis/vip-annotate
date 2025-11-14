package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.Input;

public record SpliceAiCommandArgs(
    Input input,
    Path ncbiGeneFile,
    Path faiFile,
    Path output,
    @Nullable String regionsStr,
    @Nullable Boolean force) {}
