package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record ResolvedAnnotationDbSpec(
    // TODO use SemVer class with regex, see https://semver.org
    String specVersion,
    // TODO use [a-z0-9._-] and length ≤ 64
    String specId,
    @Nullable String specDescription,
    ResolvedAnnotationSchema annotationSchema) {

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeString(specVersion);
    binaryWriter.writeString(specId);
    binaryWriter.writeStringNullable(specDescription);
    annotationSchema.writeTo(binaryWriter);
  }

  public static ResolvedAnnotationDbSpec readFrom(BinaryReader binaryReader) {
    String specVersion = binaryReader.readString();
    String specId = binaryReader.readString();
    String specDescription = binaryReader.readStringNullable();
    ResolvedAnnotationSchema annotationSchema = ResolvedAnnotationSchema.readFrom(binaryReader);
    return new ResolvedAnnotationDbSpec(specVersion, specId, specDescription, annotationSchema);
  }
}
