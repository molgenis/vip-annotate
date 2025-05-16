package org.molgenis.vcf.annotate.db.exact;

/**
 * Alternate non-reference allele annotations encoded in a byte array.
 *
 * @param variant alternate non-reference allele
 * @param encodedAnnotations annotations encoded in a byte array
 */
public record VariantAltAlleleAnnotation(Variant variant, byte[] encodedAnnotations) {}
