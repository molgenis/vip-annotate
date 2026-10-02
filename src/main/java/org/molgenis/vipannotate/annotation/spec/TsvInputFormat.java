package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jspecify.annotations.Nullable;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
@JsonTypeName("tsv")
public record TsvInputFormat(
    @JsonProperty(value = "header", required = true) boolean header,
    @JsonProperty(value = "coordinate_system", required = true) CoordinateSystem coordinateSystem,
    @JsonProperty(value = "missing_value") @Nullable String missingValue,
    @JsonProperty(value = "columns", required = true) TsvColumns columns)
    implements InputFormat {
  @Override
  public AnnotationType annotationType() {
    boolean hasRef = columns().ref() != null;
    boolean hasAlt = columns().alt() != null;
    boolean hasEnd = columns().end() != null;

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
