package org.molgenis.vipannotate.annotation.ncer;

import java.nio.charset.StandardCharsets;
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
      // FIXME handle all alt cases
      // Each allele in this list must be one of: a non-empty String of bases (A,C,G,T,N;case
      // insensitive); the ‘*’ symbol (allele missing due to overlapping deletion); the MISSING
      // value ‘.’ (no variant);an angle-bracketed ID String (“<ID>”); the unspecified allele “<*>”
      // as described in Section 5.5; or a breakend replacement string as described in Section 5.4
      DoubleValueAnnotation altAnnotation =
              annotationDb.findAnnotations(
                      new SequenceVariant(contig, start, stop, alt.getBytes(StandardCharsets.UTF_8)));
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
