package org.molgenis.vipannotate.annotation.remm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.NumberCollections;

// TODO refactor: deduplicate ncer,phylop,remm annotator
@RequiredArgsConstructor
public class RemmAnnotator implements VcfRecordAnnotator {
  public static final String INFO_ID_REMM = "REMM";

  private final PositionAnnotationDb<DoubleValueAnnotation> annotationDb;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    vcfHeader
        .vcfMetaInfo()
        .addOrUpdateInfo(INFO_ID_REMM, "1", "Float", "REMM score", App.getName(), App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.chrom());
    int start = vcfRecord.pos();
    int stop = vcfRecord.pos() + vcfRecord.ref().length() - 1;
    String[] alts = vcfRecord.alt();

    List<@Nullable DoubleValueAnnotation> altsAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      Collection<DoubleValueAnnotation> altAnnotations =
          annotationDb.findAnnotations(
              new SequenceVariant(
                  contig,
                  start,
                  stop,
                  AltAlleleRegistry.get(alt),
                  SequenceVariant.fromVcfString(vcfRecord.ref().length(), alt)));

      // for multi-nucleotide substitutions/deletions, select the annotation with max score
      DoubleValueAnnotation maxAltAnnotation =
          NumberCollections.findMax(altAnnotations, DoubleValueAnnotation::score);
      altsAnnotations.add(maxAltAnnotation);
    }

    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord, altsAnnotations, INFO_ID_REMM, DoubleValueAnnotation::score, "#.###");
  }

  @Override
  public void close() {
    annotationDb.close();
  }
}
