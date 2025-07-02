package org.molgenis.vipannotate.annotation.spliceai;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;

@RequiredArgsConstructor
public class SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper {
  private final ContigRegistry contigRegistry;

  public @Nullable SpliceAiAnnotatedSequenceVariant annotate(SpliceAiVcfRecord spliceAiVcfRecord) {
    SequenceVariant variant = createVariant(spliceAiVcfRecord);
    if (variant == null) return null;
    SpliceAiAnnotation annotation = createAnnotation(spliceAiVcfRecord);
    return new SpliceAiAnnotatedSequenceVariant(variant, annotation);
  }

  private @Nullable SequenceVariant createVariant(SpliceAiVcfRecord spliceAiVcfRecord) {
    String chromStr = "chr" + spliceAiVcfRecord.chr();

    Contig contig = contigRegistry.getContig(chromStr);
    if (contig == null) {
      return null;
    }

    String ref = spliceAiVcfRecord.ref();
    String alt = spliceAiVcfRecord.alt();
    if (alt.indexOf('N') != -1) {
      return null; // FIXME see VariantEncoder.encodeAltBase
    }
    int start = spliceAiVcfRecord.pos();
    int end = start + ref.length() - 1;
    AltAllele altAllele = AltAlleleRegistry.get(alt);
    SequenceVariantType type = SequenceVariant.fromVcfString(ref.length(), alt);
    return new SequenceVariant(contig, start, end, altAllele, type);
  }

  private static SpliceAiAnnotation createAnnotation(SpliceAiVcfRecord spliceAiVcfRecord) {
    return new SpliceAiAnnotation(
        spliceAiVcfRecord.deltaScoreAcceptorGain(),
        spliceAiVcfRecord.deltaScoreAcceptorLoss(),
        spliceAiVcfRecord.deltaScoreDonorGain(),
        spliceAiVcfRecord.deltaScoreDonorLoss(),
        spliceAiVcfRecord.deltaPositionAcceptorGain(),
        spliceAiVcfRecord.deltaPositionAcceptorLoss(),
        spliceAiVcfRecord.deltaPositionDonorGain(),
        spliceAiVcfRecord.deltaPositionDonorLoss());
  }
}
