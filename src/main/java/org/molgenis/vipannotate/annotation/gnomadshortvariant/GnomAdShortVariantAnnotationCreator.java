package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationData.Filter;
import org.molgenis.vipannotate.annotation.gnomadshortvariant.GnomAdShortVariantAnnotationData.Source;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.v2.VariantAnnotation;

public class GnomAdShortVariantAnnotationCreator {
  public VariantAnnotation<GnomAdShortVariantAnnotationData> annotate(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord) {
    Variant variant = createVariant(gnomAdShortVariantTsvRecord);
    GnomAdShortVariantAnnotationData annotation = createAnnotation(gnomAdShortVariantTsvRecord);
    return new VariantAnnotation<>(variant, annotation);
  }

  private static Variant createVariant(GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord) {
    String chrom = gnomAdShortVariantTsvRecord.chrom();
    int start = gnomAdShortVariantTsvRecord.pos();
    int end = start + gnomAdShortVariantTsvRecord.ref().length() - 1;
    byte[] alt = gnomAdShortVariantTsvRecord.alt().getBytes(StandardCharsets.UTF_8);
    return new Variant(chrom, start, end, alt);
  }

  private static GnomAdShortVariantAnnotationData createAnnotation(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord) {
    Source source = createAnnotationSource(gnomAdShortVariantTsvRecord);
    double af = createAnnotationAf(gnomAdShortVariantTsvRecord, source);
    double faf95 = createAnnotationFaf95(gnomAdShortVariantTsvRecord, source);
    double faf99 = createAnnotationFaf99(gnomAdShortVariantTsvRecord, source);
    int hn = createAnnotationHn(gnomAdShortVariantTsvRecord, source);
    EnumSet<Filter> filters = createAnnotationFilters(gnomAdShortVariantTsvRecord, source);
    double cov = createAnnotationCov(gnomAdShortVariantTsvRecord, source);
    return new GnomAdShortVariantAnnotationData(source, af, faf95, faf99, hn, filters, cov);
  }

  private static Source createAnnotationSource(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord) {
    boolean notCalledInExomes = gnomAdShortVariantTsvRecord.notCalledInExomes();
    boolean notCalledInGenomes = gnomAdShortVariantTsvRecord.notCalledInGenomes();

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

  private static double createAnnotationAf(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> {
        Double af = gnomAdShortVariantTsvRecord.afExomes();
        // FIXME investigate possible bug in gnomAD data e.g. a variant near chr3/86
        yield af != null ? af : 0;
      }
      case GENOMES -> {
        Double af = gnomAdShortVariantTsvRecord.afGenomes();
        // FIXME investigate possible bug in gnomAD data e.g. 21-5029882-CAA-A
        yield af != null ? af : 0;
      }
      case TOTAL -> {
        Double af = gnomAdShortVariantTsvRecord.afJoint();
        // FIXME investigate possible bug in gnomAD data e.g. 21-5087539-G-A
        yield af != null ? gnomAdShortVariantTsvRecord.afJoint() : 0;
      }
    };
  }

  private static double createAnnotationFaf95(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariantTsvRecord.faf95Exomes();
      case GENOMES -> gnomAdShortVariantTsvRecord.faf95Genomes();
      case TOTAL -> gnomAdShortVariantTsvRecord.faf95Joint();
    };
  }

  private static double createAnnotationFaf99(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariantTsvRecord.faf99Exomes();
      case GENOMES -> gnomAdShortVariantTsvRecord.faf99Genomes();
      case TOTAL -> gnomAdShortVariantTsvRecord.faf99Joint();
    };
  }

  private static int createAnnotationHn(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariantTsvRecord.nhomaltExomes();
      case GENOMES -> gnomAdShortVariantTsvRecord.nhomaltGenomes();
      case TOTAL -> gnomAdShortVariantTsvRecord.nhomaltJoint();
    };
  }

  private static EnumSet<Filter> createAnnotationFilters(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> mapFilters(gnomAdShortVariantTsvRecord.exomesFilters());
      case GENOMES -> mapFilters(gnomAdShortVariantTsvRecord.genomesFilters());
      case TOTAL -> {
        EnumSet<Filter> mappedFilters = EnumSet.noneOf(Filter.class);
        mappedFilters.addAll(mapFilters(gnomAdShortVariantTsvRecord.exomesFilters()));
        mappedFilters.addAll(mapFilters(gnomAdShortVariantTsvRecord.genomesFilters()));
        yield mappedFilters;
      }
    };
  }

  private static EnumSet<Filter> mapFilters(EnumSet<GnomAdShortVariantTsvRecord.Filter> filters) {
    EnumSet<Filter> mappedFilters = EnumSet.noneOf(Filter.class);
    filters.forEach(filter -> mappedFilters.add(mapFilter(filter)));
    return mappedFilters;
  }

  private static Filter mapFilter(GnomAdShortVariantTsvRecord.Filter filter) {
    return switch (filter) {
      case AC0 -> Filter.AC0;
      case AS_VQSR -> Filter.AS_VQSR;
      case INBREEDING_COEFF -> Filter.INBREEDING_COEFF;
    };
  }

  private static double createAnnotationCov(
      GnomAdShortVariantTsvRecord gnomAdShortVariantTsvRecord, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariantTsvRecord.covExomes();
      case GENOMES -> gnomAdShortVariantTsvRecord.covGenomes();
      case TOTAL -> gnomAdShortVariantTsvRecord.covJoint();
    };
  }
}
