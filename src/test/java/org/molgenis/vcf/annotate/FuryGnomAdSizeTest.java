// package org.molgenis.vcf.annotate;
//
// import static java.util.Objects.requireNonNull;
//
// import java.io.Serial;
// import java.io.Serializable;
// import java.util.HashMap;
// import java.util.Map;
// import lombok.EqualsAndHashCode;
// import lombok.Getter;
// import lombok.NonNull;
// import lombok.Value;
// import lombok.experimental.SuperBuilder;
// import org.apache.fury.*;
// import org.apache.fury.config.*;
// import org.apache.fury.logging.LoggerFactory;
// import org.molgenis.vcf.annotate.db2.gnomad.model.ClosedInterval;
// import org.molgenis.vcf.annotate.util.Quantized16UnitIntervalDouble;
// import org.molgenis.vcf.annotate.util.Quantized16UnitIntervalDoublePrimitive;
//
// public class FuryGnomAdSizeTest {
//
//  public static void main(String[] args) {
//    LoggerFactory.useSlf4jLogging(true);
//
//    Fury fury =
//        Fury.builder()
//            .withLanguage(Language.JAVA)
//            .withIntCompressed(true)
//            .withCompatibleMode(CompatibleMode.SCHEMA_CONSISTENT)
//            .requireClassRegistration(true)
//            .build();
//    fury.register(FilterV1.class);
//    fury.register(FilterV2.class);
//    fury.register(GnomAdShortVariantV1.class);
//    fury.register(GnomAdShortVariantV2.class);
//    fury.register(GnomAdShortVariantV3.class);
//    fury.register(GnomAdShortVariantV4.class);
//    fury.register(GnomAdShortVariantV5.class);
//    fury.register(GnomAdShortVariantV6.class);
//    fury.register(GnomAdShortVariantV7.class);
//    fury.register(GnomAdShortVariantV7.VariantData.class);
//
//    System.out.printf(
//        "v1 min: %d bytes%n", fury.serialize(GnomAdShortVariantV1.createMin()).length); // 74
// bytes
//    System.out.printf(
//        "v1 max: %d bytes%n", fury.serialize(GnomAdShortVariantV1.createMax()).length); // 98
// bytes
//    System.out.printf(
//        "v2 min: %d bytes%n",
//        fury.serialize(GnomAdShortVariantV2.createMin()).length); // 38 bytes  = -36
//    System.out.printf(
//        "v2 max: %d bytes%n",
//        fury.serialize(GnomAdShortVariantV2.createMax()).length); // 86 bytes  = -12
//    System.out.printf(
//        "v3 min: %d bytes%n", fury.serialize(GnomAdShortVariantV3.createMin()).length); // 38
// bytes
//    System.out.printf(
//        "v3 max: %d bytes%n",
//        fury.serialize(GnomAdShortVariantV3.createMax()).length); // 89 bytes  =  +3
//    System.out.printf(
//        "v4 min: %d bytes%n",
//        fury.serialize(GnomAdShortVariantV4.createMin()).length); // 36 bytes =  -2
//    System.out.printf(
//        "v4 max: %d bytes%n",
//        fury.serialize(GnomAdShortVariantV4.createMax()).length); // 77 bytes = -12
//    System.out.printf(
//        "v5 min: %d bytes%n",
//        fury.serialize(GnomAdShortVariantV5.createMin()).length); // 26 bytes = -10
//    System.out.printf(
//        "v5 max: %d bytes%n",
//        fury.serialize(GnomAdShortVariantV5.createMax()).length); // 67 bytes = -10
//    System.out.printf(
//        "v5.1 min: %d bytes%n",
//        fury.serializeJavaObject(GnomAdShortVariantV5.createMin()).length); // 24 bytes = -2
//    System.out.printf(
//        "v5.1 max: %d bytes%n",
//        fury.serializeJavaObject(GnomAdShortVariantV5.createMax()).length); // 63 bytes = -3
//    System.out.printf(
//        "v6 min: %d bytes%n",
//        fury.serializeJavaObject(GnomAdShortVariantV6.createMin()).length); // 35 bytes = +11
//    System.out.printf(
//        "v6 max: %d bytes%n",
//        fury.serializeJavaObject(GnomAdShortVariantV6.createMax()).length); // 52 bytes = -11
//    System.out.printf(
//        "v7 min: %d bytes%n",
//        fury.serializeJavaObject(GnomAdShortVariantV7.createMin()).length); //  7 bytes = -28
//    System.out.printf(
//        "v7 max: %d bytes%n",
//        fury.serializeJavaObject(GnomAdShortVariantV7.createMax()).length); // 55 bytes = +3
//
//    // v8 min: 7 bytes = -28?
//    // v8 max: 54 bytes = -1?
//
//    // v8 with codec improvements for type and alt: 43 bytes
//
//  }
//
//  @Getter
//  public enum FilterV2 {
//    AC0("AC0"),
//    AC0_AND_AS_VQSR("AC0,AS_VQSR"),
//    AC0_AND_INBREEDING_COEFF("AC0,InbreedingCoeff"),
//    AC0_AND_AS_VQSR_AND_INBREEDING_COEFF("AC0,AS_VQSR,InbreedingCoeff"),
//    AS_VQSR("AS_VQSR"),
//    AS_VQSR_AND_INBREEDING_COEFF("AS_VQSR,InbreedingCoeff"),
//    INBREEDING_COEFF("InbreedingCoeff");
//
//    private static final Map<String, FilterV2> ID_TO_FILTER_MAP;
//
//    static {
//      ID_TO_FILTER_MAP = HashMap.newHashMap(FilterV2.values().length);
//      for (FilterV2 filter : FilterV2.values()) {
//        ID_TO_FILTER_MAP.put(filter.getId(), filter);
//      }
//    }
//
//    private final String id;
//
//    FilterV2(String id) {
//      this.id = requireNonNull(id);
//    }
//
//    public static FilterV2 from(String id) {
//      return ID_TO_FILTER_MAP.get(requireNonNull(id));
//    }
//  }
//
//  @Getter
//  public enum FilterV1 {
//    AC0("AC0"),
//    AS_VQSR("AS_VQSR"),
//    INBREEDING_COEFF("InbreedingCoeff");
//
//    private static final Map<String, org.molgenis.vcf.annotate.db2.gnomad.model.FilterV1>
//        ID_TO_FILTER_MAP;
//
//    static {
//      ID_TO_FILTER_MAP =
//          HashMap.newHashMap(org.molgenis.vcf.annotate.db2.gnomad.model.FilterV1.values().length);
//      for (org.molgenis.vcf.annotate.db2.gnomad.model.FilterV1 filter :
//          org.molgenis.vcf.annotate.db2.gnomad.model.FilterV1.values()) {
//        ID_TO_FILTER_MAP.put(filter.getId(), filter);
//      }
//    }
//
//    private final String id;
//
//    FilterV1(String id) {
//      this.id = requireNonNull(id);
//    }
//
//    public static org.molgenis.vcf.annotate.db2.gnomad.model.FilterV1 from(String id) {
//      return ID_TO_FILTER_MAP.get(requireNonNull(id));
//    }
//  }
//
//  @Value
//  @EqualsAndHashCode(callSuper = true)
//  @SuperBuilder
//  public static class GnomAdShortVariantV1 extends ClosedInterval implements Serializable {
//    public static GnomAdShortVariantV1 createMin() {
//      return GnomAdShortVariantV1.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .exomesFilters(new FilterV1[0])
//          .genomesFilters(new FilterV1[0])
//          .build();
//    }
//
//    public static GnomAdShortVariantV1 createMax() {
//      return GnomAdShortVariantV1.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .afExomes(0.3f)
//          .afGenomes(0.5f)
//          .afJoint(0.4f)
//          .faf95Exomes(0.3f)
//          .faf95Genomes(0.5f)
//          .faf95Joint(0.4f)
//          .faf99Exomes(0.3f)
//          .faf99Genomes(0.5f)
//          .faf99Joint(0.4f)
//          .nHomAltExomes(Integer.MAX_VALUE)
//          .nHomAltGenomes(Integer.MAX_VALUE)
//          .nHomAltJoint(Integer.MAX_VALUE)
//          .exomesFilters(new FilterV1[] {FilterV1.AC0, FilterV1.AS_VQSR,
// FilterV1.INBREEDING_COEFF})
//          .genomesFilters(
//              new FilterV1[] {FilterV1.AC0, FilterV1.AS_VQSR, FilterV1.INBREEDING_COEFF})
//          .notCalledInExomes(true)
//          .notCalledInGenomes(true)
//          .covExomes(0.3f)
//          .covGenomes(0.5f)
//          .covJoint(0.4f)
//          .build();
//    }
//
//    @Serial private static final long serialVersionUID = 1L;
//
//    transient String chrom;
//    @NonNull String alt;
//    float afExomes;
//    float afGenomes;
//    float afJoint;
//    float faf95Exomes;
//    float faf95Genomes;
//    float faf95Joint;
//    float faf99Exomes;
//    float faf99Genomes;
//    float faf99Joint;
//    int nHomAltExomes;
//    int nHomAltGenomes;
//    int nHomAltJoint;
//    @NonNull FilterV1[] exomesFilters;
//    @NonNull FilterV1[] genomesFilters;
//    boolean notCalledInExomes;
//    boolean notCalledInGenomes;
//    float covExomes;
//    float covGenomes;
//    float covJoint;
//  }
//
//  /** v1, but replace float with Short */
//  @Value
//  @EqualsAndHashCode(callSuper = true)
//  @SuperBuilder
//  public static class GnomAdShortVariantV2 extends ClosedInterval implements Serializable {
//    // FIXME remove closed interval, this can be retrieved using interval tree
//    @Serial private static final long serialVersionUID = 1L;
//
//    transient String chrom;
//    @NonNull String alt; // TODO: store as bit array, see sequence
//    Short quantizedAfExomes; // TODO can we introduce apache fury 'short' compression?
//    Short quantizedAfGenomes;
//    Short quantizedAfJoint;
//    Short quantizedFaf95Exomes;
//    Short quantizedFaf95Genomes;
//    Short quantizedFaf95Joint;
//    Short quantizedFaf99Exomes;
//    Short quantizedFaf99Genomes;
//    Short quantizedFaf99Joint;
//    int nHomAltExomes; // FIXME can be null
//    int nHomAltGenomes; // FIXME can be null
//    int nHomAltJoint; // FIXME can be null
//    @NonNull FilterV1[] exomesFilters; // TODO use single combined filter
//    @NonNull FilterV1[] genomesFilters; // TODO use single combined filter
//    boolean notCalledInExomes;
//    boolean notCalledInGenomes;
//    Short quantizedCovExomes;
//    Short quantizedCovGenomes;
//    Short quantizedCovJoint;
//
//    // etc. for other Short quantized*
//    public Double getAfExomes() {
//      return quantizedAfExomes != null
//          ? Quantized16UnitIntervalDoublePrimitive.toDouble(quantizedAfExomes)
//          : null;
//    }
//
//    public static GnomAdShortVariantV2 createMin() {
//      return GnomAdShortVariantV2.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .exomesFilters(new FilterV1[0])
//          .genomesFilters(new FilterV1[0])
//          .build();
//    }
//
//    public static GnomAdShortVariantV2 createMax() {
//      return GnomAdShortVariantV2.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .quantizedAfExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedAfGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedAfJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf95Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf95Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf95Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf99Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf99Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf99Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .nHomAltExomes(Integer.MAX_VALUE)
//          .nHomAltGenomes(Integer.MAX_VALUE)
//          .nHomAltJoint(Integer.MAX_VALUE)
//          .exomesFilters(new FilterV1[] {FilterV1.AC0, FilterV1.AS_VQSR,
// FilterV1.INBREEDING_COEFF})
//          .genomesFilters(
//              new FilterV1[] {FilterV1.AC0, FilterV1.AS_VQSR, FilterV1.INBREEDING_COEFF})
//          .notCalledInExomes(true)
//          .notCalledInGenomes(true)
//          .quantizedCovExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedCovGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedCovJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .build();
//    }
//  }
//
//  /** v2, but replace nHomAlt int with Integer */
//  @Value
//  @EqualsAndHashCode(callSuper = true)
//  @SuperBuilder
//  public static class GnomAdShortVariantV3 extends ClosedInterval implements Serializable {
//    // FIXME remove closed interval, this can be retrieved using interval tree
//    @Serial private static final long serialVersionUID = 1L;
//
//    transient String chrom;
//    @NonNull String alt; // TODO: store as bit array, see sequence
//    Short quantizedAfExomes; // TODO can we introduce apache fury 'short' compression?
//    Short quantizedAfGenomes;
//    Short quantizedAfJoint;
//    Short quantizedFaf95Exomes;
//    Short quantizedFaf95Genomes;
//    Short quantizedFaf95Joint;
//    Short quantizedFaf99Exomes;
//    Short quantizedFaf99Genomes;
//    Short quantizedFaf99Joint;
//    Integer nHomAltExomes;
//    Integer nHomAltGenomes;
//    Integer nHomAltJoint;
//    @NonNull FuryGnomAdSizeTest.FilterV1[] exomesFilters; // TODO use single combined filter
//    @NonNull FuryGnomAdSizeTest.FilterV1[] genomesFilters; // TODO use single combined filter
//    boolean notCalledInExomes;
//    boolean notCalledInGenomes;
//    Short quantizedCovExomes;
//    Short quantizedCovGenomes;
//    Short quantizedCovJoint;
//
//    // etc. for other Short quantized*
//    public Double getAfExomes() {
//      return quantizedAfExomes != null
//          ? Quantized16UnitIntervalDoublePrimitive.toDouble(quantizedAfExomes)
//          : null;
//    }
//
//    public static GnomAdShortVariantV3 createMin() {
//      return GnomAdShortVariantV3.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .exomesFilters(new FuryGnomAdSizeTest.FilterV1[0])
//          .genomesFilters(new FuryGnomAdSizeTest.FilterV1[0])
//          .build();
//    }
//
//    public static GnomAdShortVariantV3 createMax() {
//      return GnomAdShortVariantV3.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .quantizedAfExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedAfGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedAfJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf95Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf95Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf95Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf99Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf99Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf99Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .nHomAltExomes(Integer.MAX_VALUE)
//          .nHomAltGenomes(Integer.MAX_VALUE)
//          .nHomAltJoint(Integer.MAX_VALUE)
//          .exomesFilters(
//              new FuryGnomAdSizeTest.FilterV1[] {
//                FuryGnomAdSizeTest.FilterV1.AC0,
//                FuryGnomAdSizeTest.FilterV1.AS_VQSR,
//                FuryGnomAdSizeTest.FilterV1.INBREEDING_COEFF
//              })
//          .genomesFilters(
//              new FuryGnomAdSizeTest.FilterV1[] {
//                FuryGnomAdSizeTest.FilterV1.AC0,
//                FuryGnomAdSizeTest.FilterV1.AS_VQSR,
//                FuryGnomAdSizeTest.FilterV1.INBREEDING_COEFF
//              })
//          .notCalledInExomes(true)
//          .notCalledInGenomes(true)
//          .quantizedCovExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedCovGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedCovJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .build();
//    }
//  }
//
//  /** v3, but replace filter array with composed filter */
//  @Value
//  @EqualsAndHashCode(callSuper = true)
//  @SuperBuilder
//  public static class GnomAdShortVariantV4 extends ClosedInterval implements Serializable {
//    // FIXME remove closed interval, this can be retrieved using interval tree
//    @Serial private static final long serialVersionUID = 1L;
//
//    transient String chrom;
//    @NonNull String alt; // TODO: store as bit array, see sequence
//    Short quantizedAfExomes; // TODO can we introduce apache fury 'short' compression?
//    Short quantizedAfGenomes;
//    Short quantizedAfJoint;
//    Short quantizedFaf95Exomes;
//    Short quantizedFaf95Genomes;
//    Short quantizedFaf95Joint;
//    Short quantizedFaf99Exomes;
//    Short quantizedFaf99Genomes;
//    Short quantizedFaf99Joint;
//    Integer nHomAltExomes;
//    Integer nHomAltGenomes;
//    Integer nHomAltJoint;
//    FilterV2 exomesFilters;
//    FilterV2 genomesFilters;
//    boolean notCalledInExomes;
//    boolean notCalledInGenomes;
//    Short quantizedCovExomes;
//    Short quantizedCovGenomes;
//    Short quantizedCovJoint;
//
//    // etc. for other Short quantized*
//    public Double getAfExomes() {
//      return quantizedAfExomes != null
//          ? Quantized16UnitIntervalDoublePrimitive.toDouble(quantizedAfExomes)
//          : null;
//    }
//
//    public static GnomAdShortVariantV4 createMin() {
//      return GnomAdShortVariantV4.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .build();
//    }
//
//    public static GnomAdShortVariantV4 createMax() {
//      return GnomAdShortVariantV4.builder()
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .chrom("chr1")
//          .alt("A")
//          .quantizedAfExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedAfGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedAfJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf95Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf95Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf95Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf99Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf99Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf99Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .nHomAltExomes(Integer.MAX_VALUE)
//          .nHomAltGenomes(Integer.MAX_VALUE)
//          .nHomAltJoint(Integer.MAX_VALUE)
//          .exomesFilters(FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//          .genomesFilters(FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//          .notCalledInExomes(true)
//          .notCalledInGenomes(true)
//          .quantizedCovExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedCovGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedCovJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .build();
//    }
//  }
//
//  /** v4, but without closed interval */
//  @Value
//  @EqualsAndHashCode
//  @SuperBuilder
//  public static class GnomAdShortVariantV5 implements Serializable {
//    // FIXME remove closed interval, this can be retrieved using interval tree
//    @Serial private static final long serialVersionUID = 1L;
//
//    transient String chrom;
//    transient int start;
//    transient int length;
//    @NonNull String alt; // TODO: store as bit array, see sequence
//    Short quantizedAfExomes; // TODO can we introduce apache fury 'short' compression?
//    Short quantizedAfGenomes;
//    short quantizedAfJoint;
//    Short quantizedFaf95Exomes;
//    Short quantizedFaf95Genomes;
//    Short quantizedFaf95Joint;
//    Short quantizedFaf99Exomes;
//    Short quantizedFaf99Genomes;
//    Short quantizedFaf99Joint;
//    Integer nHomAltExomes;
//    Integer nHomAltGenomes;
//    Integer nHomAltJoint;
//    FilterV2 exomesFilters;
//    FilterV2 genomesFilters;
//    boolean notCalledInExomes;
//    boolean notCalledInGenomes;
//    Short quantizedCovExomes;
//    Short quantizedCovGenomes;
//    Short quantizedCovJoint;
//
//    // etc. for other Short quantized*
//    public Double getAfExomes() {
//      return quantizedAfExomes != null
//          ? Quantized16UnitIntervalDoublePrimitive.toDouble(quantizedAfExomes)
//          : null;
//    }
//
//    public static GnomAdShortVariantV5 createMin() {
//      return GnomAdShortVariantV5.builder().chrom("chr1").alt("A").build();
//    }
//
//    public static GnomAdShortVariantV5 createMax() {
//      return GnomAdShortVariantV5.builder()
//          .chrom("chr1")
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .alt("A")
//          .quantizedAfExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedAfGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedAfJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf95Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf95Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf95Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .quantizedFaf99Exomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedFaf99Genomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedFaf99Joint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .nHomAltExomes(Integer.MAX_VALUE)
//          .nHomAltGenomes(Integer.MAX_VALUE)
//          .nHomAltJoint(Integer.MAX_VALUE)
//          .exomesFilters(FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//          .genomesFilters(FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//          .notCalledInExomes(true)
//          .notCalledInGenomes(true)
//          .quantizedCovExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedCovGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedCovJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .build();
//    }
//  }
//
//  /** v5, but with short primitives that can contain null state */
//  @Value
//  @EqualsAndHashCode
//  @SuperBuilder
//  public static class GnomAdShortVariantV6 implements Serializable {
//    // FIXME remove closed interval, this can be retrieved using interval tree
//    @Serial private static final long serialVersionUID = 1L;
//
//    transient String chrom;
//    transient int start;
//    transient int length;
//    @NonNull String alt; // TODO: store as bit array, see sequence
//    short quantizedAfExomes; // TODO can we introduce apache fury 'short' compression?
//    short quantizedAfGenomes;
//    short quantizedAfJoint;
//    short quantizedFaf95Exomes;
//    short quantizedFaf95Genomes;
//    short quantizedFaf95Joint;
//    short quantizedFaf99Exomes;
//    short quantizedFaf99Genomes;
//    short quantizedFaf99Joint;
//    Integer nHomAltExomes;
//    Integer nHomAltGenomes;
//    Integer nHomAltJoint;
//    FuryGnomAdSizeTest.FilterV2 exomesFilters;
//    FuryGnomAdSizeTest.FilterV2 genomesFilters;
//    boolean notCalledInExomes;
//    boolean notCalledInGenomes;
//    short quantizedCovExomes;
//    short quantizedCovGenomes;
//    short quantizedCovJoint;
//
//    // etc. for other Short quantized*
//    public Double getAfExomes() {
//      return Quantized16UnitIntervalDouble.toDouble(quantizedAfExomes);
//    }
//
//    public static GnomAdShortVariantV6 createMin() {
//      return GnomAdShortVariantV6.builder().chrom("chr1").alt("A").build();
//    }
//
//    public static GnomAdShortVariantV6 createMax() {
//      return GnomAdShortVariantV6.builder()
//          .chrom("chr1")
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .alt("A")
//          .quantizedAfExomes(Quantized16UnitIntervalDouble.toShort(0.3))
//          .quantizedAfGenomes(Quantized16UnitIntervalDouble.toShort(0.5))
//          .quantizedAfJoint(Quantized16UnitIntervalDouble.toShort(0.4))
//          .quantizedFaf95Exomes(Quantized16UnitIntervalDouble.toShort(0.3))
//          .quantizedFaf95Genomes(Quantized16UnitIntervalDouble.toShort(0.5))
//          .quantizedFaf95Joint(Quantized16UnitIntervalDouble.toShort(0.4))
//          .quantizedFaf99Exomes(Quantized16UnitIntervalDouble.toShort(0.3))
//          .quantizedFaf99Genomes(Quantized16UnitIntervalDouble.toShort(0.5))
//          .quantizedFaf99Joint(Quantized16UnitIntervalDouble.toShort(0.4))
//          .nHomAltExomes(Integer.MAX_VALUE)
//          .nHomAltGenomes(Integer.MAX_VALUE)
//          .nHomAltJoint(Integer.MAX_VALUE)
//          .exomesFilters(FuryGnomAdSizeTest.FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//          .genomesFilters(FuryGnomAdSizeTest.FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//          .notCalledInExomes(true)
//          .notCalledInGenomes(true)
//          .quantizedCovExomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//          .quantizedCovGenomes(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//          .quantizedCovJoint(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//          .build();
//    }
//  }
//
//  /** V5, but with separate exomes/genomes data */
//  @Value
//  @EqualsAndHashCode
//  @SuperBuilder
//  public static class GnomAdShortVariantV7 implements Serializable {
//    @Serial private static final long serialVersionUID = 1L;
//
//    transient String chrom;
//    transient int start;
//    transient int length;
//    @NonNull String alt; // TODO: store as bit array, see sequence
//
//    VariantData exomes;
//    VariantData genomes;
//    VariantData joint;
//
//    /** stop in closed interval [start, stop] */
//    public int getStop() {
//      return start + length - 1;
//    }
//
//    @Value
//    @EqualsAndHashCode
//    @SuperBuilder
//    public static class VariantData implements Serializable {
//      @Serial private static final long serialVersionUID = 1L;
//
//      short quantizedAf;
//      short quantizedFaf95;
//      short quantizedFaf99;
//      short quantizedCov;
//      Integer nHomAlt;
//      FilterV2 filters;
//    }
//
//    public static GnomAdShortVariantV7 createMin() {
//      return GnomAdShortVariantV7.builder().chrom("chr1").alt("A").build();
//    }
//
//    public static GnomAdShortVariantV7 createMax() {
//      return FuryGnomAdSizeTest.GnomAdShortVariantV7.builder()
//          .chrom("chr1")
//          .start(Integer.MAX_VALUE)
//          .length(Integer.MAX_VALUE)
//          .alt("A")
//          .exomes(
//              VariantData.builder()
//                  .quantizedAf(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//                  .quantizedFaf95(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//                  .quantizedFaf99(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//                  .quantizedCov(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//                  .nHomAlt(Integer.MAX_VALUE)
//                  .filters(FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//                  .build())
//          .genomes(
//              VariantData.builder()
//                  .quantizedAf(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//                  .quantizedFaf95(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//                  .quantizedFaf99(Quantized16UnitIntervalDoublePrimitive.toShort(0.3))
//                  .quantizedCov(Quantized16UnitIntervalDoublePrimitive.toShort(0.5))
//                  .nHomAlt(Integer.MAX_VALUE)
//                  .filters(FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//                  .build())
//          .joint(
//              VariantData.builder()
//                  .quantizedAf(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//                  .quantizedFaf95(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//                  .quantizedFaf99(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//                  .quantizedCov(Quantized16UnitIntervalDoublePrimitive.toShort(0.4))
//                  .nHomAlt(Integer.MAX_VALUE)
//                  .filters(FilterV2.AC0_AND_AS_VQSR_AND_INBREEDING_COEFF)
//                  .build())
//          .build();
//    }
//  }
// }
