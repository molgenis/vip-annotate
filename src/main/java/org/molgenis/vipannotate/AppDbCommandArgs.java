package org.molgenis.vipannotate;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.Input;

/** {@link org.molgenis.vipannotate.AppDb} command-line arguments shared between commands */
public record AppDbCommandArgs(
    Input input, Path faiFile, Path output, @Nullable String regionsStr, @Nullable Boolean force) {}
