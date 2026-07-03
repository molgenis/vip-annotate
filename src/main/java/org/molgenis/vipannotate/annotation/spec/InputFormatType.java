package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum InputFormatType {
  @JsonProperty("bed")
  BED,
  @JsonProperty("tsv")
  TSV,
  @JsonProperty("vcf")
  VCF
}
