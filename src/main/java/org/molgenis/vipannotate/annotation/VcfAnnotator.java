package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfReader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.format.vcf.VcfWriter;
import org.molgenis.vipannotate.util.PredicateBatchIterator;

@RequiredArgsConstructor
public class VcfAnnotator implements AutoCloseable {
  private static final int ANNOTATE_BATCH_SIZE = 100; // TODO make configurable

  private final VcfReader vcfReader;
  private final VcfRecordAnnotator vcfRecordAnnotator;
  private final VcfWriter vcfWriter;

  public void annotate() {
    List<VcfRecord> reusableBatchList = new ArrayList<>(ANNOTATE_BATCH_SIZE);
    PredicateBatchIterator<VcfRecord> batchIterator =
        new PredicateBatchIterator<>(
            vcfReader,
            (currentBatch, nextItem) -> currentBatch.size() < ANNOTATE_BATCH_SIZE,
            reusableBatchList);

    // update header
    VcfHeader vcfHeader = vcfReader.getHeader();
    vcfRecordAnnotator.updateHeader(vcfHeader);
    vcfWriter.writeHeader(vcfHeader);

    // update records
    while (batchIterator.hasNext()) {
      List<VcfRecord> batch = batchIterator.next();
      vcfRecordAnnotator.annotate(batch);
      vcfWriter.write(batch);
    }
  }

  @Override
  public void close() {
    vcfWriter.close();
    vcfRecordAnnotator.close();
    vcfReader.close();
  }
}
