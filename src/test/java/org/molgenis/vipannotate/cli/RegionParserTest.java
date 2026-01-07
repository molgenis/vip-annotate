package org.molgenis.vipannotate.cli;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.annotation.Region;

@ExtendWith(MockitoExtension.class)
class RegionParserTest {
  @Mock private ContigRegistry contigRegistry;
  private RegionParser regionParser;

  @BeforeEach
  void setUp() {
    regionParser = new RegionParser(contigRegistry);
  }

  @Test
  void parseTokenContigSingle() {
    Contig contig = mock(Contig.class);
    when(contigRegistry.getContig("chr1")).thenReturn(contig);
    List<Region> regions = regionParser.parse("chr1");
    assertEquals(List.of(new Region(contig)), regions);
  }

  @Test
  void parseTokenContigStartSingle() {
    Contig contig = mock(Contig.class);
    when(contig.getLength()).thenReturn(null);
    doReturn(contig).when(contigRegistry).getContig("chr1");
    List<Region> regions = regionParser.parse("chr1:123");
    assertEquals(List.of(new Region(contig, 123)), regions);
  }

  @Test
  void parseTokenContigStartStopSingle() {
    Contig contig = mock(Contig.class);
    when(contig.getLength()).thenReturn(null);
    doReturn(contig).when(contigRegistry).getContig("chr1");
    List<Region> regions = regionParser.parse("chr1:123-456");
    assertEquals(List.of(new Region(contig, 123, 456)), regions);
  }

  @Test
  void parseTokenMultiple() {
    Contig contig1 = mock(Contig.class);
    when(contig1.getLength()).thenReturn(null);
    doReturn(contig1).when(contigRegistry).getContig("chr1");
    Contig contig2 = mock(Contig.class);
    when(contig2.getLength()).thenReturn(null);
    doReturn(contig2).when(contigRegistry).getContig("chr2");
    List<Region> regions = regionParser.parse("chr1:123-456,chr2:234-567");
    assertEquals(List.of(new Region(contig1, 123, 456), new Region(contig2, 234, 567)), regions);
  }

  @Test
  void parseTokenEmpty() {
    assertThrows(ArgValidationException.class, () -> regionParser.parse(""));
  }

  @Test
  void parseTokenInvalidContig() {
    assertThrows(ArgValidationException.class, () -> regionParser.parse("chr3"));
  }
}
