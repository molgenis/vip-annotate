package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class HgncToNcbiGeneIdMapperTest {

  @Test
  void map() throws URISyntaxException {
    Path ncbiGeneTsvFile =
        Paths.get(getClass().getClassLoader().getResource("ncbi_gene_valid.tsv").toURI());
    HgncToNcbiGeneIdMapper hgncToNcbiGeneIdMapper = HgncToNcbiGeneIdMapper.create(ncbiGeneTsvFile);
    assertAll(
        () -> assertEquals(7157, hgncToNcbiGeneIdMapper.map("TP53")),
        () -> assertEquals(1956, hgncToNcbiGeneIdMapper.map("EGFR")),
        () -> assertEquals(348, hgncToNcbiGeneIdMapper.map("APOE")),
        () -> assertEquals(7124, hgncToNcbiGeneIdMapper.map("TNF")),
        () -> assertNull(hgncToNcbiGeneIdMapper.map("unknown_ncbi_gene_symbol")));
  }

  @Test
  void createInvalid() throws URISyntaxException {
    Path ncbiGeneTsvFile =
        Paths.get(getClass().getClassLoader().getResource("ncbi_gene_invalid.tsv").toURI());
    assertThrows(UncheckedIOException.class, () -> HgncToNcbiGeneIdMapper.create(ncbiGeneTsvFile));
  }

  @Test
  void createEmpty() throws URISyntaxException {
    Path ncbiGeneTsvFile =
        Paths.get(getClass().getClassLoader().getResource("ncbi_gene_invalid_empty.tsv").toURI());
    assertThrows(UncheckedIOException.class, () -> HgncToNcbiGeneIdMapper.create(ncbiGeneTsvFile));
  }
}
