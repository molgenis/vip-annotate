package org.molgenis.vipannotate.annotation;

/**
 * @param contig contig identifier
 * @param pos 1-based start position
 * @param score
 */
public record ContigPosAnnotation(String contig, int pos, String score) {}
