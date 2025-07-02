package org.molgenis.vipannotate.annotation.phylop;

import java.nio.file.Path;

public record PhylopCommandArgs(Path inputFile, Path faiFile, Path outputFile) {}
