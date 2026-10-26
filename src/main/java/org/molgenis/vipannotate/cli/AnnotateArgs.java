package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.VcfType;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Output;

/**
 * parsed annotate command-line arguments.
 *
 * @param inputVcf input vcf
 * @param annotationFiles annotation files.
 * @param outputVcf output vcf
 * @param force whether to overwrite the output vcf if it exists.
 */
public record AnnotateArgs(
    Input inputVcf,
    List<Path> annotationFiles,
    Output outputVcf,
    @Nullable Boolean force,
    @Nullable VcfType vcfType) {}
