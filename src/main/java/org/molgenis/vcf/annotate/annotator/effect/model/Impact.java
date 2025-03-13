package org.molgenis.vcf.annotate.annotator.effect.model;

/**
 * @see <a
 *     href="https://www.ensembl.org/info/genome/variation/prediction/predicted_data.html">Ensembl
 *     Variation - Calculated variant consequences</a>
 */
public enum Impact {
  /**
   * The variant is assumed to have high (disruptive) impact in the protein, probably causing
   * protein truncation, loss of function or triggering nonsense mediated decay.
   */
  HIGH,
  /** A non-disruptive variant that might change protein effectiveness. */
  MODERATE,
  /** Assumed to be mostly harmless or unlikely to change protein behaviour. */
  LOW,
  /**
   * Usually non-coding variants or variants affecting non-coding genes, where predictions are
   * difficult or there is no evidence of impact.
   */
  MODIFIER
}
