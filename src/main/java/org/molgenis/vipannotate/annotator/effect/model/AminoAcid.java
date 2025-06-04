package org.molgenis.vipannotate.annotator.effect.model;

import static java.util.Objects.requireNonNull;

import lombok.Getter;

@Getter
public enum AminoAcid {
  ALA("Ala"),
  ARG("Arg"),
  ASN("Asn"),
  ASP("Asp"),
  CYS("Cys"),
  GLN("Gln"),
  GLU("Glu"),
  GLY("Gly"),
  HIS("His"),
  ILE("Ile"),
  LEU("Leu"),
  LYS("Lys"),
  MET("Met"),
  PHE("Phe"),
  PRO("Pro"),
  SER("Ser"),
  THR("Thr"),
  TRP("Trp"),
  TYR("Tyr"),
  VAL("Val");

  private final String term;

  AminoAcid(String term) {
    this.term = requireNonNull(term);
  }
}
