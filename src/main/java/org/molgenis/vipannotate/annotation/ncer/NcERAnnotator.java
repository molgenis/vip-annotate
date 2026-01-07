package org.molgenis.vipannotate.annotation.ncer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.NumberCollections;

// TODO refactor: deduplicate ncer,phylop,remm annotator
@RequiredArgsConstructor
public class NcERAnnotator extends BaseVcfRecordAnnotator<DoubleValueAnnotation> {
  public static final String INFO_ID_NCER = "ncER";

  private final PositionAnnotationDb<DoubleValueAnnotation> annotationDb;
  private boolean isNewAnnotation;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    isNewAnnotation =
        vcfHeader
            .vcfMetaInfo()
            .addOrUpdateInfo(
                INFO_ID_NCER,
                "A",
                "Float",
                "ncER score",
                AppMetadata.getName(),
                AppMetadata.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.getChrom().getIdentifier().toString());
    int refLength = vcfRecord.getRef().getBaseCount();
    int start = vcfRecord.getPos().get();
    int stop = vcfRecord.getPos().get() + refLength - 1;
    Alt alt = vcfRecord.getAlt();
    List<AltAllele> altAlleles = alt.getAlleles();

    VcfInfoSubfieldValueBuilder infoSubfieldBuilder = getVcfInfoSubfieldBuilder();
    for (AltAllele altAllele : altAlleles) {
      List<DoubleValueAnnotation> altAnnotations = getAltAnnotationList();
      annotationDb.findAnnotations(
          new SequenceVariant(
              contig,
              start,
              stop,
              altAllele,
              SequenceVariantTypeDetector.determineType(
                  vcfRecord.getRef().getBaseCount(), altAllele)),
          altAnnotations);

      appendAltAnnotations(altAnnotations, infoSubfieldBuilder);
    }

    writeInfoSubField(vcfRecord, INFO_ID_NCER, infoSubfieldBuilder, isNewAnnotation);
  }

  private static void appendAltAnnotations(
      List<DoubleValueAnnotation> altAnnotations, VcfInfoSubfieldValueBuilder infoSubFieldBuilder) {
    if (altAnnotations.isEmpty()) {
      infoSubFieldBuilder.appendValueMissing();
      return;
    }

    // for multi-nucleotide substitutions/deletions, select the annotation with max score
    DoubleValueAnnotation altAnnotation =
        altAnnotations.size() == 1
            ? altAnnotations.getFirst()
            : NumberCollections.findMax(altAnnotations, DoubleValueAnnotation::score);

    if (altAnnotation == null) {
      infoSubFieldBuilder.appendValueMissing();
      return;
    }

    Double score = altAnnotation.score();
    if (score == null) {
      infoSubFieldBuilder.appendValueMissing();
      return;
    }

    infoSubFieldBuilder.appendValue(score, 4);
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationDb);
  }
}
