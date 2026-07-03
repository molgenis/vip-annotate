package org.molgenis.vipannotate.annotation;

public record CompositeAnnotation(ScalarAnnotation[] annotations) implements Annotation {}
