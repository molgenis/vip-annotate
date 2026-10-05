package org.molgenis.vipannotate.annotation;

import java.util.EnumSet;
import java.util.Map;

public record InputAnalyses(
    Map<String, ContigAnalysis> contigAnalyses,
    EnumSet<SequenceVariantType> sequenceVariantTypes,
    Map<String, FieldAnalysis> annotationAnalyses) {}
