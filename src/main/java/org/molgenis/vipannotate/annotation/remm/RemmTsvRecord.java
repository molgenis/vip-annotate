package org.molgenis.vipannotate.annotation.remm;

/**
 * @param chr chromosome
 * @param start start position (1-based)
 * @param score Regulatory Mendelian Mutation (ReMM) score
 */
public record RemmTsvRecord(String chr, int start, double score) {}
