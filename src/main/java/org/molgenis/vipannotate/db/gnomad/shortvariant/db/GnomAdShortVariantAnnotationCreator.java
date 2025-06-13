package org.molgenis.vipannotate.db.gnomad.shortvariant.db;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationData;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationData.Filter;
import org.molgenis.vipannotate.db.gnomad.shortvariant.GnomAdShortVariantAnnotationData.Source;
import org.molgenis.vipannotate.db.v2.VariantAnnotation;

public class GnomAdShortVariantAnnotationCreator {
  public VariantAnnotation<GnomAdShortVariantAnnotationData> annotate(
      GnomAdShortVariant gnomAdShortVariant) {
    Variant variant = createVariant(gnomAdShortVariant);
    GnomAdShortVariantAnnotationData annotation = createAnnotation(gnomAdShortVariant);
    return new VariantAnnotation<>(variant, annotation);
  }

  private static Variant createVariant(GnomAdShortVariant gnomAdShortVariant) {
    String chrom = gnomAdShortVariant.chrom();
    int start = gnomAdShortVariant.pos();
    int end = start + gnomAdShortVariant.ref().length() - 1;
    byte[] alt = gnomAdShortVariant.alt().getBytes(StandardCharsets.UTF_8);
    return new Variant(chrom, start, end, alt);
  }

  private static GnomAdShortVariantAnnotationData createAnnotation(
      GnomAdShortVariant gnomAdShortVariant) {
    Source source = createAnnotationSource(gnomAdShortVariant);
    double af = createAnnotationAf(gnomAdShortVariant, source);
    double faf95 = createAnnotationFaf95(gnomAdShortVariant, source);
    double faf99 = createAnnotationFaf99(gnomAdShortVariant, source);
    int hn = createAnnotationHn(gnomAdShortVariant, source);
    EnumSet<Filter> filters = createAnnotationFilters(gnomAdShortVariant, source);
    double cov = createAnnotationCov(gnomAdShortVariant, source);
    return new GnomAdShortVariantAnnotationData(source, af, faf95, faf99, hn, filters, cov);
  }

  private static Source createAnnotationSource(GnomAdShortVariant gnomAdShortVariant) {
    boolean notCalledInExomes = gnomAdShortVariant.notCalledInExomes();
    boolean notCalledInGenomes = gnomAdShortVariant.notCalledInGenomes();

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

  private static double createAnnotationAf(GnomAdShortVariant gnomAdShortVariant, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariant.afExomes();
      case GENOMES -> {
        Double af = gnomAdShortVariant.afGenomes();
        // FIXME investigate possible bug in gnomAD data e.g. 21-5029882-CAA-A
        yield af != null ? af : 0;
      }
      case TOTAL -> {
        Double af = gnomAdShortVariant.afJoint();
        // FIXME investigate possible bug in gnomAD data e.g. 21-5087539-G-A
        yield af != null ? gnomAdShortVariant.afJoint() : 0;
      }
    };
  }

  private static double createAnnotationFaf95(
      GnomAdShortVariant gnomAdShortVariant, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariant.faf95Exomes();
      case GENOMES -> gnomAdShortVariant.faf95Genomes();
      case TOTAL -> gnomAdShortVariant.faf95Joint();
    };
  }

  private static double createAnnotationFaf99(
      GnomAdShortVariant gnomAdShortVariant, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariant.faf99Exomes();
      case GENOMES -> gnomAdShortVariant.faf99Genomes();
      case TOTAL -> gnomAdShortVariant.faf99Joint();
    };
  }

  private static int createAnnotationHn(GnomAdShortVariant gnomAdShortVariant, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariant.nhomaltExomes();
      case GENOMES -> gnomAdShortVariant.nhomaltGenomes();
      case TOTAL -> gnomAdShortVariant.nhomaltJoint();
    };
  }

  private static EnumSet<Filter> createAnnotationFilters(
      GnomAdShortVariant gnomAdShortVariant, Source source) {
    return switch (source) {
      case EXOMES -> mapFilters(gnomAdShortVariant.exomesFilters());
      case GENOMES -> mapFilters(gnomAdShortVariant.genomesFilters());
      case TOTAL -> {
        EnumSet<Filter> mappedFilters = EnumSet.noneOf(Filter.class);
        mappedFilters.addAll(mapFilters(gnomAdShortVariant.exomesFilters()));
        mappedFilters.addAll(mapFilters(gnomAdShortVariant.genomesFilters()));
        yield mappedFilters;
      }
    };
  }

  private static EnumSet<Filter> mapFilters(EnumSet<GnomAdShortVariant.Filter> filters) {
    EnumSet<Filter> mappedFilters = EnumSet.noneOf(Filter.class);
    filters.forEach(filter -> mappedFilters.add(mapFilter(filter)));
    return mappedFilters;
  }

  private static Filter mapFilter(GnomAdShortVariant.Filter filter) {
    return switch (filter) {
      case AC0 -> Filter.AC0;
      case AS_VQSR -> Filter.AS_VQSR;
      case INBREEDING_COEFF -> Filter.INBREEDING_COEFF;
    };
  }

  private static double createAnnotationCov(GnomAdShortVariant gnomAdShortVariant, Source source) {
    return switch (source) {
      case EXOMES -> gnomAdShortVariant.covExomes();
      case GENOMES -> gnomAdShortVariant.covGenomes();
      case TOTAL -> gnomAdShortVariant.covJoint();
    };
  }
}
