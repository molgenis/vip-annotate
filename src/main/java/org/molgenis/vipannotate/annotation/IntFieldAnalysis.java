package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.IntAnnotationSpec;

public record IntFieldAnalysis(IntAnnotationSpec annotationSpec, IntAnnotationStats stats)
    implements FieldAnalysis {}
