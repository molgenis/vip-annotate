package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Output;

public record SpliceAiCommandArgs(
    Input input,
    Path ncbiGeneFile,
    Path faiFile,
    Output output,
    @Nullable String regionsStr,
    @Nullable Boolean force) {}
