package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public record DoubleValueAnnotation(@Nullable Double score) implements Annotation {}
