package org.molgenis.vipannotate.annotation.remm;

import java.nio.file.Path;

public record RemmCommandArgs(Path inputFile, Path faiFile, Path outputFile) {}
