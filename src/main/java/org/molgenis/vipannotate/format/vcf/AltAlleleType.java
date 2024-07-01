package org.molgenis.vipannotate.format.vcf;

public enum AltAlleleType {
  /// a non-empty String of bases (A,C,G,T,N; case-insensitive)
  BASES,
  /// breakend replacement
  ///
  /// | REF | ALT    | Meaning                                                  |
  /// |-----|--------|----------------------------------------------------------|
  /// | s   | t\[p\[ | piece extending to the right of p is joined after t      |
  /// | s   | t\]p\] | reverse comp piece extending left of p is joined after t |
  /// | s   | \]p\]t | piece extending to the left of p is joined before t      |
  /// | s   | \[p\[t | reverse comp piece extending right of p is joined before |
  BREAKEND_REPLACEMENT,
  /// missing value '.' (no variant)
  MISSING,
  /// allele missing due to overlapping deletion '*'
  MISSING_OVERLAPPING_DELETION,
  /// breakend that is not part of a novel adjacency e.g. '.A' or 'T.'
  SINGLE_BREAKEND,
  ///  symbolic / angle-bracketed ID string '<ID>'
  SYMBOLIC,
  /// unspecified allele '<*>'
  UNSPECIFIED
}
