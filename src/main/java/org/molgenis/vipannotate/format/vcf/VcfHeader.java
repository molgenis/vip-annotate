package org.molgenis.vipannotate.format.vcf;

import java.io.Writer;

public record VcfHeader(VcfMetaInfo vcfMetaInfo, VcfHeaderLine vcfHeaderLine) {
  public int getNrSamples() {
    String[] fieldIds = vcfHeaderLine.line().split("\t", -1);
    return fieldIds.length == 8 ? 0 : fieldIds.length - 9;
  }

  public void write(Writer writer) {
    vcfMetaInfo.write(writer);
    vcfHeaderLine.write(writer);
  }
}
