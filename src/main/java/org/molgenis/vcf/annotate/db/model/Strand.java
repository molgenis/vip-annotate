package org.molgenis.vcf.annotate.db.model;

import java.io.Serializable;

public enum Strand implements Serializable {
  PLUS,
  MINUS,
  UNKNOWN;

  public static Strand from(String str) {
    return switch (str) {
      case "+" -> PLUS;
      case "-" -> MINUS;
      default -> throw new IllegalArgumentException("Unknown strand: " + str);
    };
  }
}
