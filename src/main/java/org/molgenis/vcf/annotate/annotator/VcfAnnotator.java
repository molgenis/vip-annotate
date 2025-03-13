package org.molgenis.vcf.annotate.annotator;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vcf.annotate.util.Logger;

@RequiredArgsConstructor
public class VcfAnnotator implements AutoCloseable {
  @NonNull private final VCFIterator reader;
  @NonNull private final List<VcfRecordAnnotator> vcfRecordAnnotators;
  @NonNull private final VariantContextWriter writer;

  /**
   * @return number of annotated vcf records
   */
  public long annotate() {
    // update header
    VCFHeader vcfHeader = reader.getHeader();
    vcfRecordAnnotators.forEach(annotator -> annotator.updateHeader(vcfHeader));
    writer.writeHeader(vcfHeader);

    // update records
    long records;
    for (records = 1; reader.hasNext(); records++) {
      VariantContext vcfRecord = reader.next();

      VariantContextBuilder vcfRecordBuilder = new VariantContextBuilder(vcfRecord);
      vcfRecordAnnotators.forEach(annotator -> annotator.annotate(vcfRecord, vcfRecordBuilder));
      writer.add(vcfRecordBuilder.make());

      if (records % 100000 == 0) Logger.info("annotated %d vcf records", records);
    }
    return records;
  }

  @Override
  public void close() {
    try {
      reader.close();
    } finally {
      writer.close();
    }
  }
}
