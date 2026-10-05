package org.molgenis.vipannotate.annotation.def;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.LogicalType;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AnnotationDbDefReader {
  private final ObjectMapper objectMapper;

  public AnnotationDbDef readFrom(MemoryBuffer memoryBuffer) {
    byte[] byteArray = memoryBuffer.getByteArray();
    return objectMapper.readValue(byteArray, AnnotationDbDef.class);
  }

  public static AnnotationDbDefReader create() {
    JsonMapper jsonMapper =
        JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
            .disable(
                DeserializationFeature
                    .FAIL_ON_NULL_FOR_PRIMITIVES) // workaround to allow leaving out nullable:false
            .withCoercionConfig(
                LogicalType.Integer,
                config -> config.setCoercion(CoercionInputShape.Float, CoercionAction.Fail))
            .build();
    return new AnnotationDbDefReader(jsonMapper);
  }
}
