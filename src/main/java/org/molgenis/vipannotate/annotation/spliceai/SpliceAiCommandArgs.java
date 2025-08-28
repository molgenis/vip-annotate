package org.molgenis.vipannotate.annotation.spliceai;

import java.nio.file.Path;

public record SpliceAiCommandArgs(
    Path inputFile, Path ncbiGeneFile, Path faiFile, Path outputFile) {}
