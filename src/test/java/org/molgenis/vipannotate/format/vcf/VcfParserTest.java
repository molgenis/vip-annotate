package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VcfParserTest {
  @Mock private VcfHeader vcfHeader;
  @Mock private VcfRecordBatchIterator vcfRecordBatchIterator;
  private VcfParser vcfParser;

  @BeforeEach
  void setUp() {
    vcfParser = new VcfParser(vcfHeader, vcfRecordBatchIterator);
  }

  @AfterEach
  void tearDown() {
    vcfParser.close();
    verify(vcfRecordBatchIterator).close();
  }

  @Test
  void hasNext() {
    when(vcfRecordBatchIterator.hasNext()).thenReturn(true);
    assertTrue(vcfParser.hasNext());
  }

  @Test
  void next() {
    List<VcfRecord> vcfRecords = List.of(mock(VcfRecord.class));
    when(vcfRecordBatchIterator.next()).thenReturn(vcfRecords);
    assertEquals(vcfRecords, vcfParser.next());
  }

  @Test
  void getHeader() {
    assertEquals(vcfHeader, vcfParser.getHeader());
  }
}
