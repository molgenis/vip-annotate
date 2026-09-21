package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.FloatAnnotationSpec;

public record FloatAnnotationAnalysis(
    FloatAnnotationSpec annotationSpec, FloatAnnotationStats stats) implements AnnotationAnalysis {}
