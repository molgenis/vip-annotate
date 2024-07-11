package org.molgenis.vcf.annotate.util;

import org.molgenis.vcf.annotate.model.Codon;

public record CodonVariant(Codon ref, Codon alt) {}
