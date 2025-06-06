package org.molgenis.vipannotate.vcf;

import lombok.NonNull;

public record VcfHeader(@NonNull VcfMetaInfo vcfMetaInfo, @NonNull VcfHeaderLine vcfHeaderLine) {}
