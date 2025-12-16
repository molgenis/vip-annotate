package org.molgenis.vipannotate.format.vcf;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.util.ZeroCopyBufferedWriter;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
@ExtendWith(MockitoExtension.class)
class VcfWriterTest {
  @Mock private ZeroCopyBufferedWriter zeroCopyBufferedWriter;
  private VcfWriter vcfWriter;

  @BeforeEach
  void setUp() {
    vcfWriter = new VcfWriter(zeroCopyBufferedWriter);
  }

  @AfterEach
  void tearDown() {
    vcfWriter.close();
  }

  @Test
  void writeHeader() {
    VcfHeader vcfHeader = mock(VcfHeader.class);
    vcfWriter.writeHeader(vcfHeader);
    verify(vcfHeader).write(zeroCopyBufferedWriter);
    verify(zeroCopyBufferedWriter).flushBuffer();
  }

  @Test
  void writeVcfRecord() {
    VcfRecord vcfRecord = mock(VcfRecord.class);
    vcfWriter.write(vcfRecord);
    verify(vcfRecord).write(zeroCopyBufferedWriter);
    verify(zeroCopyBufferedWriter).flushBuffer();
  }

  @Test
  void writeVcfRecordList() {
    VcfRecord vcfRecord0 = mock(VcfRecord.class);
    VcfRecord vcfRecord1 = mock(VcfRecord.class);
    List<VcfRecord> vcfRecordList = List.of(vcfRecord0, vcfRecord1);
    vcfWriter.write(vcfRecordList);
    verify(vcfRecord0).write(zeroCopyBufferedWriter);
    verify(vcfRecord1).write(zeroCopyBufferedWriter);
    verify(zeroCopyBufferedWriter).flushBuffer();
  }
}
