package org.molgenis.vipannotate.annotation.ncer;

/**
 * @param chr chromosome
 * @param start start position (0-based, inclusive)
 * @param end end position (0-based, exclusive)
 * @param perc genome-wide ncER percentile. The higher the percentile, the more likely essential (in
 *     terms of regulation) the region is.
 */
public record NcERBedFeature(String chr, int start, int end, double perc) {}
