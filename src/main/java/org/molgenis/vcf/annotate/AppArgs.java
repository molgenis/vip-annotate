package org.molgenis.vcf.annotate;

import java.nio.file.Path;
import lombok.NonNull;

/**
 * @param inputVcf input vcf or {@code null} to read from stdin
 * @param annotationsZip input annotation database zip
 * @param outputVcf output vcf or {@code null} to write to stdout
 */
public record AppArgs(Path inputVcf, @NonNull Path annotationsZip, Path outputVcf, Boolean force) {}
