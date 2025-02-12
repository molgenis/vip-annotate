package org.molgenis.vcf.annotate.db2.exact;

/**
 * Alternate non-reference allele annotations encoded in a byte array.
 *
 * @param variantAltAllele alternate non-reference allele
 * @param encodedAnnotations annotations encoded in a byte array
 */
public record VariantAltAlleleAnnotation(
    VariantAltAllele variantAltAllele, byte[] encodedAnnotations) {}
