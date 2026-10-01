package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.def.IntAnnotationDef;

public record IntFieldAnalysis(IntAnnotationDef annotationSpec, IntAnnotationStats stats)
    implements FieldAnalysis {}
