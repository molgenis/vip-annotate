package org.molgenis.vcf.annotate.annotator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vcf.annotate.util.ReusableBatchIterator;
import org.molgenis.vcf.annotate.vcf.VcfHeader;
import org.molgenis.vcf.annotate.vcf.VcfReader;
import org.molgenis.vcf.annotate.vcf.VcfRecord;
import org.molgenis.vcf.annotate.vcf.VcfWriter;

// TODO consider annotation of records in batches to improve performance
@RequiredArgsConstructor
public class VcfAnnotator implements AutoCloseable {
  @NonNull private final VcfReader vcfReader;
  @NonNull private final VcfRecordAnnotator vcfRecordAnnotator;
  @NonNull private final VcfWriter vcfWriter;

  public void annotate() {
    ReusableBatchIterator<VcfRecord> batchIterator = new ReusableBatchIterator<>(vcfReader, 100);

    // update header
    VcfHeader vcfHeader = vcfReader.getHeader();
    vcfRecordAnnotator.updateHeader(vcfHeader);
    vcfWriter.writeHeader(vcfHeader);

    // update records

    while (batchIterator.hasNext()) {
      Iterable<VcfRecord> batch = batchIterator.next();
      vcfRecordAnnotator.annotate(batch);
      vcfWriter.write(batch);
    }
  }

  @Override
  public void close() throws Exception {
    vcfWriter.close();
    vcfRecordAnnotator.close();
    vcfReader.close();
  }
}
