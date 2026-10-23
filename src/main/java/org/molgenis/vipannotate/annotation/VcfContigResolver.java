package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.Chrom;
import org.molgenis.vipannotate.format.vcf.ChromType;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.CharSequenceUtils;

@RequiredArgsConstructor
public class VcfContigResolver {
  @Nullable private Contig lastContig;

  public Contig getContig(VcfRecord vcfRecord) {
    Chrom chrom = vcfRecord.getChrom();
    CharSequence chromIdentifier = chrom.getIdentifierRaw();
    if (lastContig == null || !CharSequenceUtils.equals(lastContig.getName(), chromIdentifier)) {
      // perf: only check whether chrom is symbolic in case contig name differs from last config
      if (chrom.getType() == ChromType.SYMBOLIC) {
        throw new UnsupportedOperationException();
      }
      lastContig = new Contig(chromIdentifier.toString(), 1); // FIXME
    }
    return lastContig;
  }
}
