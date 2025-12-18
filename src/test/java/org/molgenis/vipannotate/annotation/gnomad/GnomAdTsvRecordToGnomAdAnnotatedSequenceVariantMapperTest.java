package org.molgenis.vipannotate.annotation.gnomad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;
import org.molgenis.vipannotate.format.vcf.AltAllele;

@ExtendWith(MockitoExtension.class)
class GnomAdTsvRecordToGnomAdAnnotatedSequenceVariantMapperTest {
  @Mock FastaIndex fastaIndex;
  private GnomAdTsvRecordToGnomAdAnnotatedSequenceVariantMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new GnomAdTsvRecordToGnomAdAnnotatedSequenceVariantMapper(fastaIndex);
  }

  @Test
  void annotateExome() {
    FastaIndexRecord fastaIndexRecord = mock(FastaIndexRecord.class);
    when(fastaIndexRecord.name()).thenReturn("chr1");
    when(fastaIndexRecord.length()).thenReturn(234);
    when(fastaIndex.get("chr1")).thenReturn(fastaIndexRecord);

    GnomAdTsvRecord gnomAdTsvRecord =
        new GnomAdTsvRecord(
            "chr1",
            123,
            "A",
            "C",
            0.1d,
            0.2d,
            0.3d,
            1.1d,
            1.2d,
            1.3d,
            2.1d,
            2.2d,
            2.3d,
            12,
            23,
            34,
            EnumSet.of(GnomAdTsvRecord.Filter.AC0),
            EnumSet.of(GnomAdTsvRecord.Filter.AS_VQSR, GnomAdTsvRecord.Filter.INBREEDING_COEFF),
            false,
            true,
            3.1d,
            3.2d,
            3.3d);
    SequenceVariant sequenceVariant =
        new SequenceVariant(
            new Contig("chr1", 234), 123, 123, new AltAllele("C"), SequenceVariantType.SNV);
    GnomAdAnnotation gnomAdAnnotation =
        new GnomAdAnnotation(
            GnomAdAnnotation.Source.EXOMES,
            0.1d,
            1.1d,
            2.1d,
            12,
            EnumSet.of(GnomAdAnnotation.Filter.AC0),
            3.1d);
    assertEquals(
        new GnomAdAnnotatedSequenceVariant(sequenceVariant, gnomAdAnnotation),
        mapper.annotate(gnomAdTsvRecord));
  }

  @Test
  void annotateGenome() {
    FastaIndexRecord fastaIndexRecord = mock(FastaIndexRecord.class);
    when(fastaIndexRecord.name()).thenReturn("chr1");
    when(fastaIndexRecord.length()).thenReturn(234);
    when(fastaIndex.get("chr1")).thenReturn(fastaIndexRecord);

    GnomAdTsvRecord gnomAdTsvRecord =
        new GnomAdTsvRecord(
            "chr1",
            123,
            "A",
            "C",
            0.1d,
            0.2d,
            0.3d,
            1.1d,
            1.2d,
            1.3d,
            2.1d,
            2.2d,
            2.3d,
            12,
            23,
            34,
            EnumSet.of(GnomAdTsvRecord.Filter.AC0),
            EnumSet.of(GnomAdTsvRecord.Filter.AS_VQSR, GnomAdTsvRecord.Filter.INBREEDING_COEFF),
            true,
            false,
            3.1d,
            3.2d,
            3.3d);
    SequenceVariant sequenceVariant =
        new SequenceVariant(
            new Contig("chr1", 234), 123, 123, new AltAllele("C"), SequenceVariantType.SNV);
    GnomAdAnnotation gnomAdAnnotation =
        new GnomAdAnnotation(
            GnomAdAnnotation.Source.GENOMES,
            0.2d,
            1.2d,
            2.2d,
            23,
            EnumSet.of(GnomAdAnnotation.Filter.AS_VQSR, GnomAdAnnotation.Filter.INBREEDING_COEFF),
            3.2d);
    assertEquals(
        new GnomAdAnnotatedSequenceVariant(sequenceVariant, gnomAdAnnotation),
        mapper.annotate(gnomAdTsvRecord));
  }

  @Test
  void annotateJoint() {
    FastaIndexRecord fastaIndexRecord = mock(FastaIndexRecord.class);
    when(fastaIndexRecord.name()).thenReturn("chr1");
    when(fastaIndexRecord.length()).thenReturn(234);
    when(fastaIndex.get("chr1")).thenReturn(fastaIndexRecord);

    GnomAdTsvRecord gnomAdTsvRecord =
        new GnomAdTsvRecord(
            "chr1",
            123,
            "A",
            "C",
            0.1d,
            0.2d,
            0.3d,
            1.1d,
            1.2d,
            1.3d,
            2.1d,
            2.2d,
            2.3d,
            12,
            23,
            34,
            EnumSet.of(GnomAdTsvRecord.Filter.AC0),
            EnumSet.of(GnomAdTsvRecord.Filter.AS_VQSR, GnomAdTsvRecord.Filter.INBREEDING_COEFF),
            false,
            false,
            3.1d,
            3.2d,
            3.3d);
    SequenceVariant sequenceVariant =
        new SequenceVariant(
            new Contig("chr1", 234), 123, 123, new AltAllele("C"), SequenceVariantType.SNV);
    GnomAdAnnotation gnomAdAnnotation =
        new GnomAdAnnotation(
            GnomAdAnnotation.Source.TOTAL,
            0.3d,
            1.3d,
            2.3d,
            34,
            EnumSet.of(
                GnomAdAnnotation.Filter.AC0,
                GnomAdAnnotation.Filter.AS_VQSR,
                GnomAdAnnotation.Filter.INBREEDING_COEFF),
            3.3d);
    assertEquals(
        new GnomAdAnnotatedSequenceVariant(sequenceVariant, gnomAdAnnotation),
        mapper.annotate(gnomAdTsvRecord));
  }

  @Test
  void annotateUnknownContig() {
    GnomAdTsvRecord gnomAdTsvRecord =
        new GnomAdTsvRecord(
            "chr1",
            123,
            "A",
            "C",
            0.1d,
            0.2d,
            0.3d,
            1.1d,
            1.2d,
            1.3d,
            2.1d,
            2.2d,
            2.3d,
            12,
            23,
            34,
            EnumSet.of(GnomAdTsvRecord.Filter.AC0),
            EnumSet.of(GnomAdTsvRecord.Filter.AS_VQSR, GnomAdTsvRecord.Filter.INBREEDING_COEFF),
            false,
            true,
            3.1d,
            3.2d,
            3.3d);
    assertThrows(IllegalArgumentException.class, () -> mapper.annotate(gnomAdTsvRecord));
  }
}
