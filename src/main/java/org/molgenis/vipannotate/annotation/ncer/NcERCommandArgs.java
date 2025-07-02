package org.molgenis.vipannotate.annotation.ncer;

import java.nio.file.Path;

public record NcERCommandArgs(Path inputFile, Path faiFile, Path outputFile) {}
