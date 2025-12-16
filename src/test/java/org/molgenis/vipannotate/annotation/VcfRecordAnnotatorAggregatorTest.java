package org.molgenis.vipannotate.annotation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
@ExtendWith(MockitoExtension.class)
class VcfRecordAnnotatorAggregatorTest {
  @Mock private VcfRecordAnnotator vcfRecordAnnotator0;
  @Mock private VcfRecordAnnotator vcfRecordAnnotator1;
  private VcfRecordAnnotatorAggregator vcfRecordAnnotatorAggregator;

  @BeforeEach
  void setUp() {
    vcfRecordAnnotatorAggregator =
        new VcfRecordAnnotatorAggregator(List.of(vcfRecordAnnotator0, vcfRecordAnnotator1));
  }

  @AfterEach
  void tearDown() {
    vcfRecordAnnotatorAggregator.close();
  }

  @Test
  void updateHeader() {
    VcfHeader vcfHeader = mock(VcfHeader.class);
    vcfRecordAnnotatorAggregator.updateHeader(vcfHeader);
    verify(vcfRecordAnnotator0).updateHeader(vcfHeader);
    verify(vcfRecordAnnotator1).updateHeader(vcfHeader);
  }

  @Test
  void annotateVcfRecord() {
    VcfRecord vcfRecord = mock(VcfRecord.class);
    vcfRecordAnnotatorAggregator.annotate(vcfRecord);
    verify(vcfRecordAnnotator0).annotate(vcfRecord);
    verify(vcfRecordAnnotator1).annotate(vcfRecord);
  }

  @Test
  void annotateVcfRecordList() {
    List<VcfRecord> vcfRecordList = List.of(mock(VcfRecord.class), mock(VcfRecord.class));
    vcfRecordAnnotatorAggregator.annotate(vcfRecordList);
    verify(vcfRecordAnnotator0).annotate(vcfRecordList);
    verify(vcfRecordAnnotator1).annotate(vcfRecordList);
  }
}
