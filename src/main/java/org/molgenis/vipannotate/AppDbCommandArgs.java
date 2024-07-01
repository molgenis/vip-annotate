package org.molgenis.vipannotate;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Output;

/** {@link org.molgenis.vipannotate.AppDb} command-line arguments shared between commands */
public record AppDbCommandArgs(
    Input input,
    Path faiFile,
    Output output,
    @Nullable String regionsStr,
    @Nullable Boolean force) {}
