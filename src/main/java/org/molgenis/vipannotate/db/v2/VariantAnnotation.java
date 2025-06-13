package org.molgenis.vipannotate.db.v2;

import org.molgenis.vipannotate.db.exact.Variant;

/**
 * Alternate non-reference allele annotations.
 *
 * @param variant alternate non-reference allele
 * @param annotation annotation
 */
public record VariantAnnotation<T>(Variant variant, T annotation) {}
