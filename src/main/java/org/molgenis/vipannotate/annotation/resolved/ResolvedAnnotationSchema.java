package org.molgenis.vipannotate.annotation.resolved;

import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.spec.AnnotationSelector;
import org.molgenis.vipannotate.annotation.spec.AnnotationType;

// FIXME remove dependency on spec.AnnotationType and spec.AnnotationSelector
public record ResolvedAnnotationSchema(
    AnnotationType annotationType,
    EnumSet<SequenceVariantType> supportedVariantTypes,
    ResolvedAnnotationSpecs annotationSpecs,
    AnnotationSelector annotationSelector) {}
