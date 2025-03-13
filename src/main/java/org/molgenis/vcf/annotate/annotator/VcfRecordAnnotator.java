package org.molgenis.vcf.annotate.annotator;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeader;

public interface VcfRecordAnnotator {
  void updateHeader(VCFHeader vcfHeader);

  /**
   * @param vcfRecord VCF record
   * @param vcfRecordBuilder annotated VCF record
   */
  void annotate(VariantContext vcfRecord, VariantContextBuilder vcfRecordBuilder);
}
