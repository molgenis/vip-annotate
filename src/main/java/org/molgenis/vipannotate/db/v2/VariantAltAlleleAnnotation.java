package org.molgenis.vipannotate.db.v2;

import org.molgenis.vipannotate.db.exact.Variant;

/**
 * Alternate non-reference allele annotations
 *
 * @param variant alternate non-reference allele
 * @param variantAnnotations annotations
 */
public record VariantAltAlleleAnnotation<T>(Variant variant, T variantAnnotations) {}
