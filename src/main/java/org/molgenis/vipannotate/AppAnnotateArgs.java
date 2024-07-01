package org.molgenis.vipannotate;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Output;

/**
 * {@link AppAnnotate} parsed command-line arguments.
 *
 * @param inputVcf input vcf
 * @param annotationsDir input directory containing annotation files.
 * @param outputVcf output vcf
 * @param force whether to overwrite the output vcf if it exists.
 * @param debugMode whether to run the app in debug mode.
 */
public record AppAnnotateArgs(
    Input inputVcf,
    Path annotationsDir,
    Output outputVcf,
    @Nullable Boolean force,
    @Nullable Boolean debugMode,
    @Nullable VcfType vcfType) {}
