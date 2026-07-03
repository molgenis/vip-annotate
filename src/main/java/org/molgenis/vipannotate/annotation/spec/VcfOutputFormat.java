package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("vcf")
public record VcfOutputFormat(
    @JsonProperty("infoId") String infoId,
    @JsonProperty("infoNumber") String infoNumber,
    @JsonProperty("infoType") String infoType,
    @JsonProperty("infoDescription") String infoDescription,
    @JsonProperty("infoVersion") String infoVersion)
    implements OutputFormat {
  @Override
  public OutputFormatType type() {
    return OutputFormatType.VCF;
  }
}
