package org.molgenis.vipannotate.db.gnomad.shortvariant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.List;
import org.apache.fury.memory.MemoryBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationData;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationData.Source;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationDataSetEncoder;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationDatasetDecoder;
import org.molgenis.vipannotate.util.SizedIterator;

class GnomAdShortVariantTsvRecordAnnotationDataSetEncoderTest {
  private GnomAdShortVariantAnnotationDataSetEncoder gnomAdShortVariantAnnotationDataSetEncoder;
  private GnomAdShortVariantAnnotationDatasetDecoder gnomAdShortVariantAnnotationDatasetDecoder;

  @BeforeEach
  void setUp() {
    gnomAdShortVariantAnnotationDataSetEncoder = new GnomAdShortVariantAnnotationDataSetEncoder();
    gnomAdShortVariantAnnotationDatasetDecoder = new GnomAdShortVariantAnnotationDatasetDecoder();
  }

  @Test
  void encodeAf() {
    for (double i = 0; i < 1; i += 0.001) {
      MemoryBuffer blob =
          gnomAdShortVariantAnnotationDataSetEncoder.encodeAf(
              new SizedIterator<>(List.of(i).iterator(), 1));
      assertEquals(i, gnomAdShortVariantAnnotationDatasetDecoder.decodeAf(blob, 0), 1E-4);
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
        gnomAdShortVariantAnnotationDataSetEncoder.encodeSources(
            new SizedIterator<>(sources.iterator(), sources.size()));
    assertAll(
        () -> {
          assertEquals(2, blob.size());
          assertEquals(
              Source.GENOMES, gnomAdShortVariantAnnotationDatasetDecoder.decodeSource(blob, 0));
          assertEquals(
              Source.EXOMES, gnomAdShortVariantAnnotationDatasetDecoder.decodeSource(blob, 1));
          assertEquals(
              Source.TOTAL, gnomAdShortVariantAnnotationDatasetDecoder.decodeSource(blob, 2));
          assertEquals(
              Source.GENOMES, gnomAdShortVariantAnnotationDatasetDecoder.decodeSource(blob, 3));
          assertEquals(
              Source.EXOMES, gnomAdShortVariantAnnotationDatasetDecoder.decodeSource(blob, 4));
          assertEquals(
              Source.TOTAL, gnomAdShortVariantAnnotationDatasetDecoder.decodeSource(blob, 5));
        });
  }

  @Test
  void encodeFilters() {
    List<EnumSet<GnomAdShortVariantAnnotationData.Filter>> filters =
        List.of(
            EnumSet.noneOf(GnomAdShortVariantAnnotationData.Filter.class),
            EnumSet.of(GnomAdShortVariantAnnotationData.Filter.AC0),
            EnumSet.of(GnomAdShortVariantAnnotationData.Filter.AS_VQSR),
            EnumSet.of(GnomAdShortVariantAnnotationData.Filter.INBREEDING_COEFF),
            EnumSet.of(
                GnomAdShortVariantAnnotationData.Filter.AC0,
                GnomAdShortVariantAnnotationData.Filter.AS_VQSR),
            EnumSet.of(
                GnomAdShortVariantAnnotationData.Filter.AC0,
                GnomAdShortVariantAnnotationData.Filter.INBREEDING_COEFF),
            EnumSet.of(
                GnomAdShortVariantAnnotationData.Filter.AS_VQSR,
                GnomAdShortVariantAnnotationData.Filter.INBREEDING_COEFF),
            EnumSet.of(
                GnomAdShortVariantAnnotationData.Filter.AC0,
                GnomAdShortVariantAnnotationData.Filter.AS_VQSR,
                GnomAdShortVariantAnnotationData.Filter.INBREEDING_COEFF));

    MemoryBuffer blob =
        gnomAdShortVariantAnnotationDataSetEncoder.encodeFilters(
            new SizedIterator<>(filters.iterator(), filters.size()));
    assertAll(
        () -> {
          assertEquals(4, blob.size());
          assertEquals(
              filters.get(0), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 0));
          assertEquals(
              filters.get(1), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 1));
          assertEquals(
              filters.get(2), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 2));
          assertEquals(
              filters.get(3), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 3));
          assertEquals(
              filters.get(4), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 4));
          assertEquals(
              filters.get(5), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 5));
          assertEquals(
              filters.get(6), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 6));
          assertEquals(
              filters.get(7), gnomAdShortVariantAnnotationDatasetDecoder.decodeFilters(blob, 7));
        });
  }
}
