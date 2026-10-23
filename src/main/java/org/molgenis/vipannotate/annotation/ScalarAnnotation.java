package org.molgenis.vipannotate.annotation;

public sealed interface ScalarAnnotation extends Annotation
    permits FloatAnnotation, IntAnnotation {}
