package org.molgenis.vipannotate.annotation.fathmmmkl;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.format.vcf.AltAlleleRegistry;

@RequiredArgsConstructor
public class FathmmMklTsvRecordToFathmmMklAnnotatedSequenceVariantMapper {
  private final FastaIndex fastaIndex;

  public FathmmMklAnnotatedSequenceVariant annotate(FathmmMklTsvRecord fathmmMklTsvRecord) {
    SequenceVariant variant = createVariant(fathmmMklTsvRecord);
    FathmmMklAnnotation annotation = createAnnotation(fathmmMklTsvRecord);
    return new FathmmMklAnnotatedSequenceVariant(variant, annotation);
  }

  private SequenceVariant createVariant(FathmmMklTsvRecord fathmmMklTsvRecord) {
    FastaIndexRecord fastaIndexRecord = fastaIndex.get(fathmmMklTsvRecord.chrom());
    if (fastaIndexRecord == null) {
      throw new IllegalArgumentException(
          "unknown contig '%s'".formatted(fathmmMklTsvRecord.chrom()));
    }
    Contig chrom = new Contig(fastaIndexRecord.name(), fastaIndexRecord.length());

    String ref = fathmmMklTsvRecord.ref();
    String alt = fathmmMklTsvRecord.alt();
    int start = fathmmMklTsvRecord.pos();
    int end = start + ref.length() - 1;
    AltAllele altAllele = AltAlleleRegistry.INSTANCE.get(alt);
    SequenceVariantType type = SequenceVariantTypeDetector.determineType(ref.length(), altAllele);
    return new SequenceVariant(chrom, start, end, altAllele, type);
  }

  private static FathmmMklAnnotation createAnnotation(FathmmMklTsvRecord fathmmMklTsvRecord) {
    double score = fathmmMklTsvRecord.score();
    return new FathmmMklAnnotation(score);
  }
}
