package org.molgenis.vipannotate.format.vcf;

import lombok.NonNull;

public record VcfHeader(@NonNull VcfMetaInfo vcfMetaInfo, @NonNull VcfHeaderLine vcfHeaderLine) {}
