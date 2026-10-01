package org.molgenis.vipannotate.annotation.spec;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiConsumer;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.def.AnnotationType;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

// FIXME remove dependency on spec.AnnotationType
public record AnnotationsSpec(
    AnnotationType annotationType,
    EnumSet<SequenceVariantType> supportedVariantTypes,
    Map<String, AnnotationSpec> annotationSpecMap) {

  public boolean isEmpty() {
    return annotationSpecMap.isEmpty();
  }

  public int size() {
    return annotationSpecMap.size();
  }

  public void forEach(BiConsumer<? super String, ? super AnnotationSpec> action) {
    annotationSpecMap.forEach(action);
  }

  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putEnum(annotationType);
    memEncoder.putEnumSet(supportedVariantTypes);
    memEncoder.putMap(annotationSpecMap, MemoryEncoder::putString, AnnotationSpec::writeTo);
  }

  public static AnnotationsSpec readFrom(MemoryDecoder memDecoder) {
    AnnotationType annotationType = memDecoder.getEnum(AnnotationType.class);
    EnumSet<SequenceVariantType> supportedVariantTypes =
        memDecoder.getEnumSet(SequenceVariantType.class);
    Map<String, AnnotationSpec> annotationSpecMap =
        memDecoder.getMap(MemoryDecoder::getString, AnnotationSpec::readFrom);
    return new AnnotationsSpec(annotationType, supportedVariantTypes, annotationSpecMap);
  }
}
