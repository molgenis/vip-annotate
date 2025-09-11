package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfParser;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.format.vcf.VcfWriter;
import org.molgenis.vipannotate.util.Logger;
import org.molgenis.vipannotate.util.PredicateBatchIterator;

@RequiredArgsConstructor
public class VcfAnnotator implements AutoCloseable {
  private static final int ANNOTATE_BATCH_SIZE = 100; // TODO make configurable

  private final VcfParser vcfParser;
  private final VcfRecordAnnotator vcfRecordAnnotator;
  private final VcfWriter vcfWriter;

  public void annotate() {
    List<VcfRecord> reusableBatchList = new ArrayList<>(ANNOTATE_BATCH_SIZE);
    PredicateBatchIterator<VcfRecord> batchIterator =
        new PredicateBatchIterator<>(
            vcfParser,
            (currentBatch, nextItem) -> currentBatch.size() < ANNOTATE_BATCH_SIZE,
            reusableBatchList);

    // update header
    VcfHeader vcfHeader = vcfParser.getHeader();
    vcfRecordAnnotator.updateHeader(vcfHeader);
    vcfWriter.writeHeader(vcfHeader);

    // update records
    long start = System.currentTimeMillis();
    Logger.debug("annotating records");

    int processedRecords = 0;
    while (batchIterator.hasNext()) {
      List<VcfRecord> batch = batchIterator.next();
      vcfRecordAnnotator.annotate(batch);
      vcfWriter.write(batch);

      // log progress
      if (Logger.isDebugEnabled()) {
        processedRecords += batch.size();
        if (processedRecords % 100000 < ANNOTATE_BATCH_SIZE) {
          Logger.debug("processed %d records", processedRecords);
        }
      }
    }

    if (Logger.isDebugEnabled()) {
      // log progress (remainder)
      if (processedRecords % 100000 >= ANNOTATE_BATCH_SIZE) {
        Logger.debug("processed %d records", processedRecords);
      }
      long duration = System.currentTimeMillis() - start;
      Logger.debug(
          "annotating records took %d s, %d records/s",
          Math.divideExact(duration, 1000L), Math.divideExact(processedRecords * 1000L, duration));
    }
  }

  @Override
  public void close() {
    vcfWriter.close();
    vcfRecordAnnotator.close();
    vcfParser.close();
  }
}
