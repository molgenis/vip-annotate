package org.molgenis.vipannotate.annotation.resolved;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.LogicalType;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ResolvedAnnotationDbSpecSerializer {
  private final ObjectMapper objectMapper;

  public MemoryBuffer serialize(ResolvedAnnotationDbSpec spec) {
    byte[] byteArray = objectMapper.writeValueAsBytes(spec);
    MemoryBuffer memoryBuffer =
        MemoryBuffer.wrap(new byte[MemoryBuffer.VAR_INT_MAX_BYTE_SIZE + byteArray.length]);
    memoryBuffer.putByteArray(byteArray);
    return memoryBuffer;
  }

  public static ResolvedAnnotationDbSpecSerializer create() {
    // TODO dedeup with reader
    JsonMapper jsonMapper =
        JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(
                DeserializationFeature
                    .FAIL_ON_NULL_FOR_PRIMITIVES) // workaround to allow leaving out nullable:false
            .withCoercionConfig(
                LogicalType.Integer,
                config -> config.setCoercion(CoercionInputShape.Float, CoercionAction.Fail))
            .build();
    return new ResolvedAnnotationDbSpecSerializer(jsonMapper);
  }
}
