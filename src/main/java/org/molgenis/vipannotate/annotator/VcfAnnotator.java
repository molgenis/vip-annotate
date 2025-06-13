package org.molgenis.vipannotate.annotator;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.ReusableBatchIterator;
import org.molgenis.vipannotate.vcf.*;

@RequiredArgsConstructor
public class VcfAnnotator implements AutoCloseable {
  private static final int ANNOTATE_BATCH_SIZE = 100;

  @NonNull private final VcfReader vcfReader;
  @NonNull private final VcfRecordAnnotator vcfRecordAnnotator;
  @NonNull private final VcfWriter vcfWriter;

  public void annotate() {

    ReusableBatchIterator<VcfRecord> batchIterator =
        new ReusableBatchIterator<>(
            vcfReader, ANNOTATE_BATCH_SIZE); // FIXME do not batch over partition borders

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
