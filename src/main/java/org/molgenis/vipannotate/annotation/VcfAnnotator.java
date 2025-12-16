package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor
public class VcfAnnotator implements AutoCloseable {
  private static final int LOG_EVERY_N = 100000;

  private final VcfParser vcfParser;
  private final VcfRecordAnnotator vcfRecordAnnotator;
  private final VcfWriter vcfWriter;

  public void annotate() {
    // update header
    VcfHeader vcfHeader = vcfParser.getHeader();
    vcfRecordAnnotator.updateHeader(vcfHeader);
    vcfWriter.writeHeader(vcfHeader);

    // update records
    long start = System.nanoTime();
    Logger.debug("annotating records");

    int processedRecords = 0;
    int nextLogThreshold = LOG_EVERY_N;
    while (vcfParser.hasNext()) {
      List<VcfRecord> batch = vcfParser.next();
      vcfRecordAnnotator.annotate(batch);
      vcfWriter.write(batch);

      // log progress
      processedRecords += batch.size();
      if (Logger.isDebugEnabled() && processedRecords >= nextLogThreshold) {
        Logger.debug("processed %d records", processedRecords);
        nextLogThreshold = ((processedRecords / LOG_EVERY_N) + 1) * LOG_EVERY_N;
      }
    }

    // log progress summary
    if (Logger.isDebugEnabled()) {
      if (processedRecords < nextLogThreshold) {
        Logger.debug("processed %d records", processedRecords);
      }

      long duration = System.nanoTime() - start;
      long durationSec = duration / 1_000_000_000L;
      long recordsPerSec = duration != 0 ? (processedRecords * 1_000_000_000L) / duration : 0;
      Logger.debug("annotating records took %d s, %d records/s", durationSec, recordsPerSec);
    }
  }

  @Override
  public void close() {
    ClosableUtils.closeAll(vcfWriter, vcfRecordAnnotator, vcfParser);
  }
}
