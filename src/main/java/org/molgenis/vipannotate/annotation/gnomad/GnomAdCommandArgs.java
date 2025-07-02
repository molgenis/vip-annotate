package org.molgenis.vipannotate.annotation.gnomad;

import java.nio.file.Path;

public record GnomAdCommandArgs(Path inputFile, Path faiFile, Path outputFile) {}
