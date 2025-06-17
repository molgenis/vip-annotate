package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;

class FastaIndexParserTest {

  @Test
  void createInvalidPath() {
    assertThrows(
        UncheckedIOException.class,
        () ->
            FastaIndexParser.create(Path.of("src", "test", "resources", "does_not_exist.fna.fai")));
  }

  @Test
  void create() {
    FastaIndex fastaIndex =
        FastaIndexParser.create(
            Path.of(
                "src", "test", "resources", "GCA_000001405.15_GRCh38_no_alt_analysis_set.fna.fai"));
    assertAll(
        () -> assertTrue(fastaIndex.containsReferenceSequence("chr1")),
        () -> assertTrue(fastaIndex.containsReferenceSequence("chr1_KI270706v1_random")),
        () -> assertTrue(fastaIndex.containsReferenceSequence("chrEBV")));
  }
}
