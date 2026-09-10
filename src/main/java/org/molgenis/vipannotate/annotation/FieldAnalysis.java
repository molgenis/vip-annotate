package org.molgenis.vipannotate.annotation;

public sealed interface FieldAnalysis
    permits EnumFieldAnalysis, EnumSetFieldAnalysis, FloatFieldAnalysis, IntFieldAnalysis {}
