package org.molgenis.vcf.annotate.db.gnomad;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationCodec;

public class GnomAdShortVariantAnnotationCodec implements AnnotationCodec<GnomAdShortVariantAnnotation> {
  // TODO docs
  public MemoryBuffer encode(GnomAdShortVariantAnnotation variant) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(43);

    GnomAdShortVariantAnnotation.VariantData exomes = variant.getExomes();
    GnomAdShortVariantAnnotation.VariantData genomes = variant.getGenomes();
    GnomAdShortVariantAnnotation.VariantData joint = variant.getJoint();

    int encodedHasExomes = exomes == null ? 0 : 1;
    int encodedHasExomesNHomAlt = exomes == null || exomes.getNHomAlt() == -1 ? 0 : 1;
    int encodedHasExomesFilters = exomes == null || exomes.getFilters() == null ? 0 : 1;
    int encodedHasGenomes = genomes == null ? 0 : 1;
    int encodedHasGenomesNHomAlt = genomes == null || genomes.getNHomAlt() == -1 ? 0 : 1;
    int encodedHasGenomesFilters = genomes == null || genomes.getFilters() == null ? 0 : 1;

    // bit 1+2: type 00=nothing, 01=exomes, 10=genomes 11=exomes+genomes
    //          <-- check if 00 case exists in data
    // bit   3: 0=one or more exomes  filters failed 1=pass
    // bit   4: 0=one or more genomes filters failed 1=pass
    // bit   5: 0=exomes  hom alt is null 1=exomes  hom alt is not null
    // bit   6: 0=genomes hom alt is null 1=genomes hom alt is not null
    byte header =
        (byte)
            (encodedHasExomes << 5
                | encodedHasExomesNHomAlt << 4
                | encodedHasExomesFilters << 3
                | encodedHasGenomes << 2
                | encodedHasGenomesNHomAlt << 1
                | encodedHasGenomesFilters);

    memoryBuffer.writeByte(header);

    if (exomes != null) {
      encodeVariantData(
          exomes, exomes.getNHomAlt() != -1, exomes.getFilters() != null, memoryBuffer);
    }
    if (genomes != null) {
      encodeVariantData(
          genomes, genomes.getNHomAlt() != -1, genomes.getFilters() != null, memoryBuffer);
    }
    if (exomes != null && genomes != null) {
      // joint nhomalt can be computed from exomes + genomes
      // joint has no filters
      encodeVariantData(joint, false, false, memoryBuffer);
    }

    return memoryBuffer;
  }

  private static void encodeVariantData(
      GnomAdShortVariantAnnotation.VariantData variantData,
      boolean writeNHomAlt,
      boolean writeFilters,
      MemoryBuffer memoryBuffer) {
    memoryBuffer.writeInt16(variantData.getQuantizedAf());
    memoryBuffer.writeInt16(variantData.getQuantizedFaf95());
    memoryBuffer.writeInt16(variantData.getQuantizedFaf99());
    memoryBuffer.writeInt16(variantData.getQuantizedCov());
    if (writeNHomAlt) {
      memoryBuffer.writeVarInt32(variantData.getNHomAlt());
    }
    if (writeFilters) {
      // TODO write each filter as flag and store in EnumSet in Filter
      memoryBuffer.writeByte(
          variantData.getFilters() != null ? (byte) variantData.getFilters().ordinal() : -1);
    }
  }

  // TODO docs
  public GnomAdShortVariantAnnotation decode(MemoryBuffer memoryBuffer) {
    byte header = memoryBuffer.readByte();

    int hasExomes = header >> 5 & 1;
    int hasExomesNHomAlt = header >> 4 & 1;
    int hasExomesFilters = header >> 3 & 1;
    int hasGenomes = header >> 2 & 1;
    int hasGenomesNHomAlt = header >> 1 & 1;
    int hasGenomesFilters = header & 1;

    GnomAdShortVariantAnnotation.VariantData exomes =
        hasExomes == 1
            ? decodeVariantData(memoryBuffer, hasExomesNHomAlt == 1, hasExomesFilters == 1)
            : null;
    GnomAdShortVariantAnnotation.VariantData genomes =
        hasGenomes == 1
            ? decodeVariantData(memoryBuffer, hasGenomesNHomAlt == 1, hasGenomesFilters == 1)
            : null;
    GnomAdShortVariantAnnotation.VariantData joint =
        hasExomes == 1 && hasGenomes == 1 ? decodeVariantData(memoryBuffer, false, false) : null;

    return new GnomAdShortVariantAnnotation(exomes, genomes, joint);
  }

  private static GnomAdShortVariantAnnotation.VariantData decodeVariantData(
      MemoryBuffer memoryBuffer, boolean hasNHomAlt, boolean hasFilters) {
    short quantizedAf = memoryBuffer.readInt16();
    short quantizedFaf95 = memoryBuffer.readInt16();
    short quantizedFaf99 = memoryBuffer.readInt16();
    short quantizedCov = memoryBuffer.readInt16();
    int nHomAlt = hasNHomAlt ? memoryBuffer.readVarInt32() : -1;
    GnomAdShortVariantAnnotation.FilterV2 filter =
        hasFilters ? GnomAdShortVariantAnnotation.FilterV2.values()[memoryBuffer.readByte()] : null;
    return new GnomAdShortVariantAnnotation.VariantData(
        quantizedAf, quantizedFaf95, quantizedFaf99, quantizedCov, nHomAlt, filter);
  }
}
