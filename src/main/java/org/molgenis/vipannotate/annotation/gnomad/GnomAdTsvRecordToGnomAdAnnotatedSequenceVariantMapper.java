package org.molgenis.vipannotate.annotation.gnomad;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.SequenceVariant;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Filter;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;

@RequiredArgsConstructor
public class GnomAdTsvRecordToGnomAdAnnotatedSequenceVariantMapper {
  private final FastaIndex fastaIndex;

  public GnomAdAnnotatedSequenceVariant annotate(GnomAdTsvRecord gnomAdTsvRecord) {
    SequenceVariant variant = createVariant(gnomAdTsvRecord);
    GnomAdAnnotation annotation = createAnnotation(gnomAdTsvRecord);
    return new GnomAdAnnotatedSequenceVariant(variant, annotation);
  }

  private SequenceVariant createVariant(GnomAdTsvRecord gnomAdTsvRecord) {
    FastaIndexRecord fastaIndexRecord = fastaIndex.get(gnomAdTsvRecord.chrom());
    if (fastaIndexRecord == null) {
      throw new IllegalArgumentException("unknown contig '%s'".formatted(gnomAdTsvRecord.chrom()));
    }
    Contig chrom = new Contig(fastaIndexRecord.name(), fastaIndexRecord.length());

    String ref = gnomAdTsvRecord.ref();
    String alt = gnomAdTsvRecord.alt();
    int start = gnomAdTsvRecord.pos();
    int end = start + ref.length() - 1;
    SequenceVariantType type = SequenceVariant.fromVcfString(ref.length(), alt);
    return new SequenceVariant(chrom, start, end, alt, type);
  }

  private static GnomAdAnnotation createAnnotation(GnomAdTsvRecord gnomAdTsvRecord) {
    Source source = createAnnotationSource(gnomAdTsvRecord);
    Double af = createAnnotationAf(gnomAdTsvRecord, source);
    double faf95 = createAnnotationFaf95(gnomAdTsvRecord, source);
    double faf99 = createAnnotationFaf99(gnomAdTsvRecord, source);
    int hn = createAnnotationHn(gnomAdTsvRecord, source);
    EnumSet<Filter> filters = createAnnotationFilters(gnomAdTsvRecord, source);
    double cov = createAnnotationCov(gnomAdTsvRecord, source);
    return new GnomAdAnnotation(source, af, faf95, faf99, hn, filters, cov);
  }

  private static Source createAnnotationSource(GnomAdTsvRecord gnomAdTsvRecord) {
    boolean notCalledInExomes = gnomAdTsvRecord.notCalledInExomes();
    boolean notCalledInGenomes = gnomAdTsvRecord.notCalledInGenomes();

    Source source;
    if (!notCalledInExomes && !notCalledInGenomes) {
      source = Source.TOTAL;
    } else if (!notCalledInExomes) {
      source = Source.EXOMES;
    } else {
      source = Source.GENOMES;
      return source;
    }
    return source;
  }

  // AF can be null for the source e.g., 21-5029882-CAA-A or 21-5087539-G-A
  private static Double createAnnotationAf(GnomAdTsvRecord gnomAdTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdTsvRecord.afExomes();
      case GENOMES -> gnomAdTsvRecord.afGenomes();
      case TOTAL -> gnomAdTsvRecord.afJoint();
    };
  }

  private static double createAnnotationFaf95(GnomAdTsvRecord gnomAdTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdTsvRecord.faf95Exomes();
      case GENOMES -> gnomAdTsvRecord.faf95Genomes();
      case TOTAL -> gnomAdTsvRecord.faf95Joint();
    };
  }

  private static double createAnnotationFaf99(GnomAdTsvRecord gnomAdTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdTsvRecord.faf99Exomes();
      case GENOMES -> gnomAdTsvRecord.faf99Genomes();
      case TOTAL -> gnomAdTsvRecord.faf99Joint();
    };
  }

  private static int createAnnotationHn(GnomAdTsvRecord gnomAdTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdTsvRecord.nhomaltExomes();
      case GENOMES -> gnomAdTsvRecord.nhomaltGenomes();
      case TOTAL -> gnomAdTsvRecord.nhomaltJoint();
    };
  }

  private static EnumSet<Filter> createAnnotationFilters(
      GnomAdTsvRecord gnomAdTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> mapFilters(gnomAdTsvRecord.exomesFilters());
      case GENOMES -> mapFilters(gnomAdTsvRecord.genomesFilters());
      case TOTAL -> {
        EnumSet<Filter> mappedFilters = EnumSet.noneOf(Filter.class);
        mappedFilters.addAll(mapFilters(gnomAdTsvRecord.exomesFilters()));
        mappedFilters.addAll(mapFilters(gnomAdTsvRecord.genomesFilters()));
        yield mappedFilters;
      }
    };
  }

  private static EnumSet<Filter> mapFilters(EnumSet<GnomAdTsvRecord.Filter> filters) {
    EnumSet<Filter> mappedFilters = EnumSet.noneOf(Filter.class);
    filters.forEach(filter -> mappedFilters.add(mapFilter(filter)));
    return mappedFilters;
  }

  private static Filter mapFilter(GnomAdTsvRecord.Filter filter) {
    return switch (filter) {
      case AC0 -> Filter.AC0;
      case AS_VQSR -> Filter.AS_VQSR;
      case INBREEDING_COEFF -> Filter.INBREEDING_COEFF;
    };
  }

  private static double createAnnotationCov(GnomAdTsvRecord gnomAdTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdTsvRecord.covExomes();
      case GENOMES -> gnomAdTsvRecord.covGenomes();
      case TOTAL -> gnomAdTsvRecord.covJoint();
    };
  }
}
