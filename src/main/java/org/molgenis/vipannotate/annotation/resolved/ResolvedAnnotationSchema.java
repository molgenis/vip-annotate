package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.*;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.spec.AnnotationSelector;
import org.molgenis.vipannotate.annotation.spec.AnnotationType;

// FIXME remove dependency on spec.AnnotationType and spec.AnnotationSelector
// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
public record ResolvedAnnotationSchema(
    @JsonProperty(value = "annotation_type", required = true) AnnotationType annotationType,
    @JsonProperty(value = "supported_variant_types", required = true)
        EnumSet<SequenceVariantType> supportedVariantTypes,
    @JsonProperty(value = "annotation_datasets", required = true)
        ResolvedAnnotationSpecs annotationSpecs,
    @JsonProperty(value = "annotation_selector", required = true)
        AnnotationSelector annotationSelector) {}
