package org.molgenis.vipannotate;

import java.nio.file.Path;
import lombok.NonNull;
import org.molgenis.vipannotate.format.vcf.VcfType;

/**
 * @param inputVcf input vcf or {@code null} to read from stdin.
 * @param annotationsDir input directory containing annotation files.
 * @param outputVcf output vcf or {@code null} to write to stdout.
 * @param force whether to overwrite the output vcf if it exists.
 * @param debugMode whether to run the app in debug mode.
 */
public record AppAnnotateArgs(
    Path inputVcf,
    @NonNull Path annotationsDir,
    Path outputVcf,
    Boolean force,
    Boolean debugMode,
    VcfType vcfType) {}
