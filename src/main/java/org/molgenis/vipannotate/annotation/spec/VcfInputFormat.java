package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("vcf")
public record VcfInputFormat() implements InputFormat {
  @Override
  public InputFormatType type() {
    return InputFormatType.VCF;
  }
}
