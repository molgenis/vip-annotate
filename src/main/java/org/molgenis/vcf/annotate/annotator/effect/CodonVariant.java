package org.molgenis.vcf.annotate.annotator.effect;

import org.molgenis.vcf.annotate.annotator.effect.model.Codon;

public record CodonVariant(Codon ref, Codon alt) {}
