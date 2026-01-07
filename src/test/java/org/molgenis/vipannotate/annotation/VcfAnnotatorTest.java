package org.molgenis.vipannotate.annotation;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfParser;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.format.vcf.VcfWriter;
import org.molgenis.vipannotate.util.Logger;

@ExtendWith(MockitoExtension.class)
class VcfAnnotatorTest {
  private static boolean LOGGER_IS_DEBUG_ENABLED_PREVIOUS_STATE;

  @Mock private VcfParser vcfParser;
  @Mock private VcfRecordAnnotator vcfRecordAnnotator;
  @Mock private VcfWriter vcfWriter;
  private VcfAnnotator vcfAnnotator;

  @BeforeAll
  static void beforeAll() {
    LOGGER_IS_DEBUG_ENABLED_PREVIOUS_STATE = Logger.isDebugEnabled();
    Logger.ENABLE_DEBUG_LOGGING = true;
  }

  @AfterAll
  static void afterAll() {
    Logger.ENABLE_DEBUG_LOGGING = LOGGER_IS_DEBUG_ENABLED_PREVIOUS_STATE;
  }

  @BeforeEach
  void setUp() {
    vcfAnnotator = new VcfAnnotator(vcfParser, vcfRecordAnnotator, vcfWriter);
  }

  @AfterEach
  void tearDown() {
    vcfAnnotator.close();
  }

  @Test
  void annotate() {
    VcfHeader vcfHeader = mock(VcfHeader.class);
    when(vcfParser.getHeader()).thenReturn(vcfHeader);

    when(vcfParser.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
    List<VcfRecord> vcfRecordBatch0 = List.of(mock(VcfRecord.class), mock(VcfRecord.class));
    List<VcfRecord> vcfRecordBatch1 = List.of(mock(VcfRecord.class));
    when(vcfParser.next())
        .thenReturn(vcfRecordBatch0)
        .thenReturn(vcfRecordBatch1)
        .thenThrow(NoSuchElementException.class);

    vcfAnnotator.annotate();

    InOrder inOrder = inOrder(vcfRecordAnnotator, vcfWriter);
    inOrder.verify(vcfRecordAnnotator).updateHeader(vcfHeader);
    inOrder.verify(vcfWriter).writeHeader(vcfHeader);
    inOrder.verify(vcfRecordAnnotator).annotate(vcfRecordBatch0);
    inOrder.verify(vcfWriter).write(vcfRecordBatch0);
    inOrder.verify(vcfRecordAnnotator).annotate(vcfRecordBatch1);
    inOrder.verify(vcfWriter).write(vcfRecordBatch1);
  }
}
