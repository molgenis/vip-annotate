package org.molgenis.vipannotate.annotation.resolved;

import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.spec.AnnotationType;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

// FIXME remove dependency on spec.AnnotationType
public record ResolvedAnnotationSchema(
    AnnotationType annotationType,
    EnumSet<SequenceVariantType> supportedVariantTypes,
    ResolvedAnnotationSpecs annotationSpecs) {

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeEnum(annotationType);
    binaryWriter.writeEnumSet(supportedVariantTypes);
    annotationSpecs.writeTo(binaryWriter);
  }

  public static ResolvedAnnotationSchema readFrom(BinaryReader binaryReader) {
    AnnotationType annotationType = binaryReader.readEnum(AnnotationType.class);
    EnumSet<SequenceVariantType> supportedVariantTypes =
        binaryReader.readEnumSet(SequenceVariantType.class);
    ResolvedAnnotationSpecs annotationSpecs = ResolvedAnnotationSpecs.readFrom(binaryReader);
    return new ResolvedAnnotationSchema(annotationType, supportedVariantTypes, annotationSpecs);
  }
}
