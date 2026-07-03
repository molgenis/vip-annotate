package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.*;
import java.util.EnumSet;
import java.util.List;

public record AnnotationSchema(
    @JsonProperty(value = "annotation_type", required = true) AnnotationType annotationType,
    @JsonProperty(value = "supported_variant_types", required = true)
        EnumSet<SequenceVariantType> supportedVariantTypes,
    @JsonProperty(value = "annotation_datasets", required = true)
        List<AnnotationDataset> annotationDatasets,
    @JsonProperty(value = "annotation_selector", required = true)
        AnnotationSelector annotationSelector) {}
