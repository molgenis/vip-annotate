package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Map;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeName("tsv")
public record TsvInputFormat(
    @JsonProperty(value = "coordinate_system", required = true) CoordinateSystem coordinateSystem,
    @JsonProperty(value = "contig", required = true) int contig,
    @JsonProperty(value = "start", required = true) int start,
    @JsonProperty(value = "end") Integer end,
    @JsonProperty(value = "ref") Integer ref,
    @JsonProperty(value = "alt") Integer alt,
    @JsonProperty(value = "annotations", required = true) Map<String, Integer> annotations)
    implements InputFormat {
  @Override
  public AnnotationType annotationType() {
    boolean hasRef = ref() != null;
    boolean hasAlt = alt() != null;
    boolean hasEnd = end() != null;

    if (hasRef && hasAlt) {
      if (hasEnd) {
        throw new IllegalArgumentException(
            "'ref' and 'alt' must be defined together, and 'end' must be undefined");
      }
      return AnnotationType.SEQUENCE_VARIANT;
    }

    if (hasEnd) {
      if (hasRef || hasAlt) {
        throw new IllegalArgumentException(
            "'end' must be defined, and 'ref' and 'alt' must be undefined");
      }
      return AnnotationType.INTERVAL;
    }

    return AnnotationType.POSITION;
  }
}
