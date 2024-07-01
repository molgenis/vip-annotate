package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.Writer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
@ExtendWith(MockitoExtension.class)
class VcfHeaderTest {
  @Mock private VcfMetaInfo vcfMetaInfo;
  @Mock private VcfHeaderLine vcfHeaderLine;
  private VcfHeader vcfHeader;

  @BeforeEach
  void setUp() {
    vcfHeader = new VcfHeader(vcfMetaInfo, vcfHeaderLine);
  }

  @Test
  void getNrSamplesZero() {
    when(vcfHeaderLine.line()).thenReturn("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO");
    assertEquals(0, vcfHeader.getNrSamples());
  }

  @Test
  void getNrSamples() {
    when(vcfHeaderLine.line())
        .thenReturn("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tSAMPLE0\tSAMPLE1");
    assertEquals(2, vcfHeader.getNrSamples());
  }

  @Test
  void write() {
    Writer writer = mock(Writer.class);
    vcfHeader.write(writer);
    verify(vcfMetaInfo).write(writer);
    verify(vcfHeaderLine).write(writer);
  }
}
