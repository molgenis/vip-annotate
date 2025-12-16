package org.molgenis.vipannotate.cli;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

/**
 * parsed database-download command-line arguments
 *
 * @param outputDir
 */
@SuppressWarnings("ArrayRecordComponent")
public record DbDownloadArgs(Path outputDir, @Nullable Boolean force) {}
