package org.molgenis.vipannotate.annotation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpec;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AnnotationSpecReader {
  private final ObjectMapper objectMapper;

  public AnnotationSpec readSpec(MemoryBuffer memoryBuffer) {
    byte[] byteArray = memoryBuffer.getByteArray();
    return objectMapper.readValue(byteArray, AnnotationSpec.class);
  }

  public static AnnotationSpecReader create() {
    JsonMapper jsonMapper = JsonMapper.builder().build();
    return new AnnotationSpecReader(jsonMapper);
  }
}
