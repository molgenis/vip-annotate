package org.molgenis.vcf.annotate.annotator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vcf.annotate.util.Logger;
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

  /**
   * @return number of annotated vcf records
   */
  public long annotate() {
    // update header
    VcfHeader vcfHeader = vcfReader.getHeader();
    vcfRecordAnnotator.updateHeader(vcfHeader);
    vcfWriter.writeHeader(vcfHeader);

    // update records
    long records;
    for (records = 1; vcfReader.hasNext(); records++) {
      VcfRecord vcfRecord = vcfReader.next();
      vcfRecordAnnotator.annotate(vcfRecord);
      vcfWriter.write(vcfRecord);

      if (records % 100000 == 0) Logger.info("annotated %d vcf records", records);
    }
    return records;
  }

  @Override
  public void close() throws Exception {
    vcfWriter.close();
    vcfRecordAnnotator.close();
    vcfReader.close();
  }
}
