package org.molgenis.vipannotate.annotation.spec;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public record AnnotationDbSpec(
    // TODO use SemVer class with regex, see https://semver.org
    String version,
    // TODO use [a-z0-9._-] and length ≤ 64
    String id,
    @Nullable String description,
    AnnotationsSpec annotationsSpec,
    Map<String, PositionBinSpec> partitioningSpec) {

  public void writeTo(BinaryWriter binaryWriter) {
    binaryWriter.writeString(version);
    binaryWriter.writeString(id);
    binaryWriter.writeStringNullable(description);
    annotationsSpec.writeTo(binaryWriter);
    binaryWriter.writeMap(
        partitioningSpec,
        BinaryWriter::writeString,
        ((thisBinaryWriter, positionBinSpec) -> positionBinSpec.writeTo(thisBinaryWriter)));
  }

  public static AnnotationDbSpec readFrom(BinaryReader binaryReader) {
    String specVersion = binaryReader.readString();
    String specId = binaryReader.readString();
    String specDescription = binaryReader.readStringNullable();
    AnnotationsSpec annotationSchema = AnnotationsSpec.readFrom(binaryReader);
    Map<String, PositionBinSpec> partitioningSpec =
        binaryReader.readMap(BinaryReader::readString, PositionBinSpec::readFrom);
    return new AnnotationDbSpec(
        specVersion, specId, specDescription, annotationSchema, partitioningSpec);
  }
}
