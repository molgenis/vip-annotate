package org.molgenis.vipannotate.annotation.gnomad;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.fasta.FastaIndex;

@RequiredArgsConstructor
public class GnomAdParser {
  private final FastaIndex fastaIndex;

  public GnomAdTsvRecord parse(String[] tokens) {
    if (tokens.length != 23) {
      throw new IllegalArgumentException("GnomAd short variant parser expects 23 tokens");
    }

    int i = 0;
    String chrom = parseChrom(tokens[i++]);
    int pos = Integer.parseInt(tokens[i++]);
    String ref = tokens[i++];
    String alt = tokens[i++];
    Double afExomes = parseDouble(tokens[i++]);
    Double afGenomes = parseDouble(tokens[i++]);
    Double afJoint = parseDouble(tokens[i++]);
    Double faf95Exomes = parseDouble(tokens[i++]);
    Double faf95Genomes = parseDouble(tokens[i++]);
    Double faf95Joint = parseDouble(tokens[i++]);
    Double faf99Exomes = parseDouble(tokens[i++]);
    Double faf99Genomes = parseDouble(tokens[i++]);
    Double faf99Joint = parseDouble(tokens[i++]);
    Integer nhomaltExomes = parseInteger(tokens[i++]);
    Integer nhomaltGenomes = parseInteger(tokens[i++]);
    Integer nhomaltJoint = parseInteger(tokens[i++]);
    EnumSet<GnomAdTsvRecord.Filter> exomesFilters = parseFilters(tokens[i++]);
    EnumSet<GnomAdTsvRecord.Filter> genomesFilters = parseFilters(tokens[i++]);
    boolean notCalledInExomes = parseBoolean(tokens[i++]);
    boolean notCalledInGenomes = parseBoolean(tokens[i++]);
    Double covExomes = parseDouble(tokens[i++]);
    Double covGenomes = parseDouble(tokens[i++]);
    Double covJoint = parseDouble(tokens[i]);

    return new GnomAdTsvRecord(
        chrom,
        pos,
        ref,
        alt,
        afExomes,
        afGenomes,
        afJoint,
        faf95Exomes,
        faf95Genomes,
        faf95Joint,
        faf99Exomes,
        faf99Genomes,
        faf99Joint,
        nhomaltExomes,
        nhomaltGenomes,
        nhomaltJoint,
        exomesFilters,
        genomesFilters,
        notCalledInExomes,
        notCalledInGenomes,
        covExomes,
        covGenomes,
        covJoint);
  }

  private String parseChrom(String token) {
    if (!fastaIndex.containsReferenceSequence(token)) {
      throw new IllegalArgumentException(token + " is not a valid chrom");
    }
    return token;
  }

  private static @Nullable Double parseDouble(String token) {
    return token.isEmpty() ? null : Double.parseDouble(token);
  }

  private static @Nullable Integer parseInteger(String token) {
    return token.isEmpty() ? null : Integer.parseInt(token);
  }

  private static EnumSet<GnomAdTsvRecord.Filter> parseFilters(String token) {
    EnumSet<GnomAdTsvRecord.Filter> filters = EnumSet.noneOf(GnomAdTsvRecord.Filter.class);
    if (!token.isEmpty()) {
      String[] filterTokens = token.split(",", -1);
      for (String filterToken : filterTokens) {
        filters.add(parseFilter(filterToken));
      }
    }

    return filters;
  }

  private static GnomAdTsvRecord.Filter parseFilter(String token) {
    return switch (token) {
      case "AC0" -> GnomAdTsvRecord.Filter.AC0;
      case "AS_VQSR" -> GnomAdTsvRecord.Filter.AS_VQSR;
      case "InbreedingCoeff" -> GnomAdTsvRecord.Filter.INBREEDING_COEFF;
      default -> throw new IllegalStateException("Unexpected value: " + token);
    };
  }

  private static boolean parseBoolean(String token) {
    boolean bool;
    if (token.isEmpty()) {
      bool = false;
    } else if (token.equals("1")) {
      bool = true;
    } else {
      throw new IllegalArgumentException(token + " is not a boolean");
    }
    return bool;
  }
}
