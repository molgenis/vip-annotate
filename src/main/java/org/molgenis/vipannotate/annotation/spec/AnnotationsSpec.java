package org.molgenis.vipannotate.annotation.spec;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiConsumer;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.def.AnnotationType;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

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

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeEnum(annotationType);
    binaryWriter.writeEnumSet(supportedVariantTypes);
    binaryWriter.writeMap(annotationSpecMap, BinaryWriter::writeString, AnnotationSpec::writeTo);
  }

  public static AnnotationsSpec readFrom(BinaryReader binaryReader) {
    AnnotationType annotationType = binaryReader.readEnum(AnnotationType.class);
    EnumSet<SequenceVariantType> supportedVariantTypes =
        binaryReader.readEnumSet(SequenceVariantType.class);
    Map<String, AnnotationSpec> annotationSpecMap =
        binaryReader.readMap(BinaryReader::readString, AnnotationSpec::readFrom);
    return new AnnotationsSpec(annotationType, supportedVariantTypes, annotationSpecMap);
  }
}
