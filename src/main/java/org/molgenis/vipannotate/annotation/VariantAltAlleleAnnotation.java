package org.molgenis.vipannotate.annotation;

/**
 * Alternate non-reference allele annotations
 *
 * @param variant alternate non-reference allele
 * @param variantAnnotations annotations
 */
public record VariantAltAlleleAnnotation<T>(Variant variant, T variantAnnotations) {}
