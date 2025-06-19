package org.molgenis.vipannotate.annotation.gnomad;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.List;
import org.apache.fury.memory.MemoryBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source;
import org.molgenis.vipannotate.util.SizedIterator;

// FIXME unit test instead of integration test
class GnomAdAnnotationDatasetEncoderTest {
  private GnomAdAnnotationDatasetEncoder gnomAdAnnotationDataSetEncoder;
  private GnomAdAnnotationDatasetDecoder gnomAdAnnotationDatasetDecoder;

  @BeforeEach
  void setUp() {
    gnomAdAnnotationDataSetEncoder = new GnomAdAnnotationDatasetEncoder();
    gnomAdAnnotationDatasetDecoder = new GnomAdAnnotationDatasetDecoder();
  }

  @Test
  void encodeAf() {
    for (double i = 0; i < 1; i += 0.001) {
      MemoryBuffer blob =
          gnomAdAnnotationDataSetEncoder.encodeAf(new SizedIterator<>(List.of(i).iterator(), 1));
      assertEquals(i, gnomAdAnnotationDatasetDecoder.decodeAf(blob, 0), 1E-4);
    }
  }

  @Test
  void encodeSources() {
    List<Source> sources =
        List.of(
            Source.GENOMES,
            Source.EXOMES,
            Source.TOTAL,
            Source.GENOMES,
            Source.EXOMES,
            Source.TOTAL);
    MemoryBuffer blob =
        gnomAdAnnotationDataSetEncoder.encodeSources(
            new SizedIterator<>(sources.iterator(), sources.size()));
    assertAll(
        () -> {
          assertEquals(2, blob.size());
          assertEquals(Source.GENOMES, gnomAdAnnotationDatasetDecoder.decodeSource(blob, 0));
          assertEquals(Source.EXOMES, gnomAdAnnotationDatasetDecoder.decodeSource(blob, 1));
          assertEquals(Source.TOTAL, gnomAdAnnotationDatasetDecoder.decodeSource(blob, 2));
          assertEquals(Source.GENOMES, gnomAdAnnotationDatasetDecoder.decodeSource(blob, 3));
          assertEquals(Source.EXOMES, gnomAdAnnotationDatasetDecoder.decodeSource(blob, 4));
          assertEquals(Source.TOTAL, gnomAdAnnotationDatasetDecoder.decodeSource(blob, 5));
        });
  }

  @Test
  void encodeFilters() {
    List<EnumSet<GnomAdAnnotation.Filter>> filters =
        List.of(
            EnumSet.noneOf(GnomAdAnnotation.Filter.class),
            EnumSet.of(GnomAdAnnotation.Filter.AC0),
            EnumSet.of(GnomAdAnnotation.Filter.AS_VQSR),
            EnumSet.of(GnomAdAnnotation.Filter.INBREEDING_COEFF),
            EnumSet.of(GnomAdAnnotation.Filter.AC0, GnomAdAnnotation.Filter.AS_VQSR),
            EnumSet.of(GnomAdAnnotation.Filter.AC0, GnomAdAnnotation.Filter.INBREEDING_COEFF),
            EnumSet.of(GnomAdAnnotation.Filter.AS_VQSR, GnomAdAnnotation.Filter.INBREEDING_COEFF),
            EnumSet.of(
                GnomAdAnnotation.Filter.AC0,
                GnomAdAnnotation.Filter.AS_VQSR,
                GnomAdAnnotation.Filter.INBREEDING_COEFF));

    MemoryBuffer blob =
        gnomAdAnnotationDataSetEncoder.encodeFilters(
            new SizedIterator<>(filters.iterator(), filters.size()));
    assertAll(
        () -> {
          assertEquals(4, blob.size());
          assertEquals(filters.get(0), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 0));
          assertEquals(filters.get(1), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 1));
          assertEquals(filters.get(2), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 2));
          assertEquals(filters.get(3), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 3));
          assertEquals(filters.get(4), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 4));
          assertEquals(filters.get(5), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 5));
          assertEquals(filters.get(6), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 6));
          assertEquals(filters.get(7), gnomAdAnnotationDatasetDecoder.decodeFilters(blob, 7));
        });
  }
}
