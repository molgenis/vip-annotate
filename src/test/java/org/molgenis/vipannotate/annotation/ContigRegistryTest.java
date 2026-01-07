package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;

class ContigRegistryTest {
  @Test
  void create() {
    FastaIndex fastaIndex = mock(FastaIndex.class);
    FastaIndexRecord fastaIndexRecord0 = new FastaIndexRecord("chr1", 123, 0, 0, 0);
    FastaIndexRecord fastaIndexRecord1 = new FastaIndexRecord("chr2", 456, 0, 0, 0);
    when(fastaIndex.getRecords()).thenReturn(List.of(fastaIndexRecord0, fastaIndexRecord1));
    ContigRegistry contigRegistry = ContigRegistry.create(fastaIndex);
    assertAll(
        () -> assertEquals(new Contig("chr1", 123), contigRegistry.getContig("chr1")),
        () -> assertEquals(new Contig("chr2", 456), contigRegistry.getContig("chr2")),
        () -> assertNull(contigRegistry.getContig("chr3")));
  }
}
