package org.molgenis.vipannotate;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.VcfType;

/**
 * @param inputVcf input vcf or {@code null} to read from stdin.
 * @param annotationsDir input directory containing annotation files.
 * @param outputVcf output vcf or {@code null} to write to stdout.
 * @param force whether to overwrite the output vcf if it exists.
 * @param debugMode whether to run the app in debug mode.
 */
public record AppAnnotateArgs(
    @Nullable Path inputVcf,
    Path annotationsDir,
    @Nullable Path outputVcf,
    @Nullable Boolean force,
    @Nullable Boolean debugMode,
    @Nullable VcfType vcfType) {}
