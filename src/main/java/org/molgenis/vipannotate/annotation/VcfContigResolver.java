package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.Chrom;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.CharSequenceUtils;

@RequiredArgsConstructor
public class VcfContigResolver {
  @Nullable private Contig lastContig;

  public Contig getContig(VcfRecord vcfRecord) {
    Chrom chrom = vcfRecord.getChrom();
    CharSequence chromIdentifier = chrom.getIdentifier();
    return switch (chrom.getType()) {
      case IDENTIFIER -> getContigFromIdentifier(chromIdentifier);
      case SYMBOLIC -> throw new UnsupportedOperationException(); // FIXME implement
    };
  }

  private Contig getContigFromIdentifier(CharSequence vcfChromIdentifier) {
    if (lastContig == null || !CharSequenceUtils.equals(lastContig.getName(), vcfChromIdentifier)) {
      lastContig = new Contig(vcfChromIdentifier.toString());
    }
    return lastContig;
  }
}
