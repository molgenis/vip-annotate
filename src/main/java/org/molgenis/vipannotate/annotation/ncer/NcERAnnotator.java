package org.molgenis.vipannotate.annotation.ncer;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

// TODO refactor: deduplicate ncer,phylop,remm annotator
@RequiredArgsConstructor
public class NcERAnnotator implements VcfRecordAnnotator {
  public static final String INFO_ID_NCER = "ncER";

  @NonNull private final PositionAnnotationDb<DoubleValueAnnotation> annotationDb;
  @NonNull private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    vcfHeader
        .vcfMetaInfo()
        .addOrUpdateInfo(INFO_ID_NCER, "1", "Float", "ncER score", App.getName(), App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.chrom());
    int start = vcfRecord.pos();
    int stop = vcfRecord.pos() + vcfRecord.ref().length() - 1;
    String[] alts = vcfRecord.alt();

    List<DoubleValueAnnotation> altAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      DoubleValueAnnotation altAnnotation =
          annotationDb.findAnnotations(
              new SequenceVariant(
                  contig,
                  start,
                  stop,
                  alt,
                  SequenceVariant.fromVcfString(vcfRecord.ref().length(), alt)));
      altAnnotations.add(altAnnotation);
    }

    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord, altAnnotations, INFO_ID_NCER, DoubleValueAnnotation::score, "##.####");
  }

  @Override
  public void close() {
    annotationDb.close();
  }
}
