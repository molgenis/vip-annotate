package org.molgenis.vipannotate.annotation;

/**
 * @param value position encoding (zero-based)
 * @param bits number of bits used to encode position
 */
public record PositionEncoding(int value, int bits) {}
