package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.annotation.spec.IntAnnotationSpec;

public record IntAnnotationAnalysis(IntAnnotationSpec annotationSpec, IntAnnotationStats stats)
    implements AnnotationAnalysis {}
