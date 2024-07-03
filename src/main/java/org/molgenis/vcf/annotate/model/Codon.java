package org.molgenis.vcf.annotate.model;

import lombok.Getter;

@Getter
public enum Codon {
  AAA(AminoAcid.LYS),
  AAC(AminoAcid.ASN),
  AAG(AminoAcid.LYS),
  AAT(AminoAcid.ASN),
  ACA(AminoAcid.THR),
  ACC(AminoAcid.THR),
  ACG(AminoAcid.THR),
  ACT(AminoAcid.THR),
  AGA(AminoAcid.ARG),
  AGC(AminoAcid.SER),
  AGG(AminoAcid.ARG),
  AGT(AminoAcid.SER),
  ATA(AminoAcid.ILE),
  ATC(AminoAcid.ILE),
  ATG(AminoAcid.MET),
  ATT(AminoAcid.ILE),
  CAA(AminoAcid.GLN),
  CAC(AminoAcid.HIS),
  CAG(AminoAcid.GLN),
  CAT(AminoAcid.HIS),
  CCA(AminoAcid.PRO),
  CCC(AminoAcid.PRO),
  CCG(AminoAcid.PRO),
  CCT(AminoAcid.PRO),
  CGA(AminoAcid.ARG),
  CGC(AminoAcid.ARG),
  CGG(AminoAcid.ARG),
  CGT(AminoAcid.ARG),
  CTA(AminoAcid.LEU),
  CTC(AminoAcid.LEU),
  CTG(AminoAcid.LEU),
  CTT(AminoAcid.LEU),
  GAA(AminoAcid.GLU),
  GAC(AminoAcid.ASP),
  GAG(AminoAcid.GLU),
  GAT(AminoAcid.ASP),
  GCA(AminoAcid.ALA),
  GCC(AminoAcid.ALA),
  GCG(AminoAcid.ALA),
  GCT(AminoAcid.ALA),
  GGA(AminoAcid.GLY),
  GGC(AminoAcid.GLY),
  GGG(AminoAcid.GLY),
  GGT(AminoAcid.GLY),
  GTA(AminoAcid.VAL),
  GTC(AminoAcid.VAL),
  GTG(AminoAcid.VAL),
  GTT(AminoAcid.VAL),
  TAA(null), // stop codon
  TAC(AminoAcid.TYR),
  TAG(null), // stop codon
  TAT(AminoAcid.TYR),
  TCA(AminoAcid.SER),
  TCC(AminoAcid.SER),
  TCG(AminoAcid.SER),
  TCT(AminoAcid.SER),
  TGA(null), // stop codon
  TGC(AminoAcid.CYS),
  TGG(AminoAcid.TRP),
  TGT(AminoAcid.CYS),
  TTA(AminoAcid.LEU),
  TTC(AminoAcid.PHE),
  TTG(AminoAcid.LEU),
  TTT(AminoAcid.PHE);

  private final AminoAcid aminoAcid;

  Codon(AminoAcid aminoAcid) {
    this.aminoAcid = aminoAcid;
  }

  public boolean isStartCodon() {
    return this == Codon.ATG;
  }

  public boolean isStopCodon() {
    return aminoAcid == null;
  }

  public static Codon from(char[] nucs) {
    final char nuc0 = nucs[0];
    final char nuc1 = nucs[1];
    final char nuc2 = nucs[2];

    return switch (nuc0) {
      case 'A' ->
          switch (nuc1) {
            case 'A' ->
                switch (nuc2) {
                  case 'A' -> Codon.AAA;
                  case 'C' -> Codon.AAC;
                  case 'G' -> Codon.AAG;
                  case 'T' -> Codon.AAT;
                  default -> throw new IllegalStateException();
                };
            case 'C' ->
                switch (nuc2) {
                  case 'A' -> Codon.ACA;
                  case 'C' -> Codon.ACC;
                  case 'G' -> Codon.ACG;
                  case 'T' -> Codon.ACT;
                  default -> throw new IllegalStateException();
                };
            case 'G' ->
                switch (nuc2) {
                  case 'A' -> Codon.AGA;
                  case 'C' -> Codon.AGC;
                  case 'G' -> Codon.AGG;
                  case 'T' -> Codon.AGT;
                  default -> throw new IllegalStateException();
                };
            case 'T' ->
                switch (nuc2) {
                  case 'A' -> Codon.ATA;
                  case 'C' -> Codon.ATC;
                  case 'G' -> Codon.ATG;
                  case 'T' -> Codon.ATT;
                  default -> throw new IllegalStateException();
                };
            default -> throw new IllegalStateException();
          };
      case 'C' ->
          switch (nuc1) {
            case 'A' ->
                switch (nuc2) {
                  case 'A' -> Codon.CAA;
                  case 'C' -> Codon.CAC;
                  case 'G' -> Codon.CAG;
                  case 'T' -> Codon.CAT;
                  default -> throw new IllegalStateException();
                };
            case 'C' ->
                switch (nuc2) {
                  case 'A' -> Codon.CCA;
                  case 'C' -> Codon.CCC;
                  case 'G' -> Codon.CCG;
                  case 'T' -> Codon.CCT;
                  default -> throw new IllegalStateException();
                };
            case 'G' ->
                switch (nuc2) {
                  case 'A' -> Codon.CGA;
                  case 'C' -> Codon.CGC;
                  case 'G' -> Codon.CGG;
                  case 'T' -> Codon.CGT;
                  default -> throw new IllegalStateException();
                };
            case 'T' ->
                switch (nuc2) {
                  case 'A' -> Codon.CTA;
                  case 'C' -> Codon.CTC;
                  case 'G' -> Codon.CTG;
                  case 'T' -> Codon.CTT;
                  default -> throw new IllegalStateException();
                };
            default -> throw new IllegalStateException();
          };
      case 'G' ->
          switch (nuc1) {
            case 'A' ->
                switch (nuc2) {
                  case 'A' -> Codon.GAA;
                  case 'C' -> Codon.GAC;
                  case 'G' -> Codon.GAG;
                  case 'T' -> Codon.GAT;
                  default -> throw new IllegalStateException();
                };
            case 'C' ->
                switch (nuc2) {
                  case 'A' -> Codon.GCA;
                  case 'C' -> Codon.GCC;
                  case 'G' -> Codon.GCG;
                  case 'T' -> Codon.GCT;
                  default -> throw new IllegalStateException();
                };
            case 'G' ->
                switch (nuc2) {
                  case 'A' -> Codon.GGA;
                  case 'C' -> Codon.GGC;
                  case 'G' -> Codon.GGG;
                  case 'T' -> Codon.GGT;
                  default -> throw new IllegalStateException();
                };
            case 'T' ->
                switch (nuc2) {
                  case 'A' -> Codon.GTA;
                  case 'C' -> Codon.GTC;
                  case 'G' -> Codon.GTG;
                  case 'T' -> Codon.GTT;
                  default -> throw new IllegalStateException();
                };
            default -> throw new IllegalStateException();
          };
      case 'T' ->
          switch (nuc1) {
            case 'A' ->
                switch (nuc2) {
                  case 'A' -> Codon.TAA;
                  case 'C' -> Codon.TAC;
                  case 'G' -> Codon.TAG;
                  case 'T' -> Codon.TAT;
                  default -> throw new IllegalStateException();
                };
            case 'C' ->
                switch (nuc2) {
                  case 'A' -> Codon.TCA;
                  case 'C' -> Codon.TCC;
                  case 'G' -> Codon.TCG;
                  case 'T' -> Codon.TCT;
                  default -> throw new IllegalStateException();
                };
            case 'G' ->
                switch (nuc2) {
                  case 'A' -> Codon.TGA;
                  case 'C' -> Codon.TGC;
                  case 'G' -> Codon.TGG;
                  case 'T' -> Codon.TGT;
                  default -> throw new IllegalStateException();
                };
            case 'T' ->
                switch (nuc2) {
                  case 'A' -> Codon.TTA;
                  case 'C' -> Codon.TTC;
                  case 'G' -> Codon.TTG;
                  case 'T' -> Codon.TTT;
                  default -> throw new IllegalStateException();
                };
            default -> throw new IllegalStateException();
          };
      default -> throw new IllegalStateException();
    };
  }
}
