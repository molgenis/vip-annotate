package org.molgenis.vipannotate.annotation.spec;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public record AnnotationDbSpec(
    // TODO use SemVer class with regex, see https://semver.org
    String version,
    // TODO use [a-z0-9._-] and length ≤ 64
    String id,
    @Nullable String description,
    AnnotationsSpec annotationsSpec,
    Map<String, PositionBinSpec> partitioningSpec) {

  public void writeTo(MemoryEncoder memEncoder) {
    memEncoder.putString(version);
    memEncoder.putString(id);
    memEncoder.putStringNullable(description);
    annotationsSpec.writeTo(memEncoder);
    memEncoder.putMap(
        partitioningSpec,
        MemoryEncoder::putString,
        (thisMemEncoder, positionBinSpec) -> positionBinSpec.writeTo(thisMemEncoder));
  }

  public static AnnotationDbSpec readFrom(MemoryDecoder memDecoder) {
    String specVersion = memDecoder.getString();
    String specId = memDecoder.getString();
    String specDescription = memDecoder.getStringNullable();
    AnnotationsSpec annotationSchema = AnnotationsSpec.readFrom(memDecoder);
    Map<String, PositionBinSpec> partitioningSpec =
        memDecoder.getMap(MemoryDecoder::getString, PositionBinSpec::readFrom);
    return new AnnotationDbSpec(
        specVersion, specId, specDescription, annotationSchema, partitioningSpec);
  }
}
