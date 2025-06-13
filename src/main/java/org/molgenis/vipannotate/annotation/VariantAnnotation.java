package org.molgenis.vipannotate.annotation;

/**
 * Alternate non-reference allele annotations.
 *
 * @param variant alternate non-reference allele
 * @param annotation annotation
 */
public record VariantAnnotation<T>(Variant variant, T annotation) {}
