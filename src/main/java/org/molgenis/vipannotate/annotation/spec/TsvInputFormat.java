package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("tsv")
public record TsvInputFormat(
    @JsonProperty(value = "file", required = true) String file,
    @JsonProperty(value = "coordinate_system", required = true) CoordinateSystem coordinateSystem,
    @JsonProperty(value = "contig", required = true) int contig,
    @JsonProperty(value = "start", required = true) int start,
    @JsonProperty(value = "end") Integer end,
    @JsonProperty(value = "ref") Integer ref,
    @JsonProperty(value = "alt") Integer alt,
    @JsonProperty(value = "annotations", required = true) int[] annotations)
    implements InputFormat {
  @Override
  public InputFormatType type() {
    return InputFormatType.TSV;
  }
}
