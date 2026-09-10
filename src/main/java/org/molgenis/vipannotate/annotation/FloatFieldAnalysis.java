package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.FloatAnnotationSpec;

public record FloatFieldAnalysis(FloatAnnotationSpec annotationSpec, FloatAnnotationStats stats)
    implements FieldAnalysis {}
