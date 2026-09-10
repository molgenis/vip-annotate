package org.molgenis.vipannotate.annotation;

/** genomic feature annotation */
public sealed interface Annotation
    permits CompositeAnnotation, ScalarAnnotation, StringAnnotation, StringListAnnotation {}
