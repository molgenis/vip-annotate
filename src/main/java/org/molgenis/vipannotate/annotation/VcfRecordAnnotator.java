package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.StringView;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class VcfRecordAnnotator<T extends Annotation> implements AutoCloseableNoThrow {
  private final SequenceVariantAnnotator<T> variantAnnotator;
  private final VcfRecordAnnotationWriter<T> annotationWriter;
  private final VcfContigResolver contigRegistry;

  @Nullable private SequenceVariant reusableSequenceVariant;

  public void annotate(VcfRecord vcfRecord, AnnotationMode annotationMode) {
    Contig contig = contigRegistry.getContig(vcfRecord);
    int start = vcfRecord.getPos().getRaw();
    int stop = start + vcfRecord.getRef().getBaseCount() - 1;

    SequenceVariant reusableSequenceVariant = getReusableSequenceVariant();
    for (AltAllele altAllele : vcfRecord.getAlt().getAlleles()) {
      reusableSequenceVariant.reset(
          contig,
          start,
          stop,
          altAllele,
          SequenceVariantTypeDetector.determineType(stop - start + 1, altAllele));
      variantAnnotator.annotate(reusableSequenceVariant, annotationWriter::appendAltAnnotation);
    }

    annotationWriter.writeInfoSubField(vcfRecord, annotationMode);
  }

  public void annotate(List<VcfRecord> vcfRecords, AnnotationMode annotationMode) {
    for (VcfRecord vcfRecord : vcfRecords) {
      annotate(vcfRecord, annotationMode);
    }
  }

  private SequenceVariant getReusableSequenceVariant() {
    if (reusableSequenceVariant == null) {
      reusableSequenceVariant =
          new SequenceVariant(
              new Contig("", 1),
              0,
              0,
              new AltAllele(new StringView("")),
              SequenceVariantType.OTHER);
    }
    return reusableSequenceVariant;
  }

  @Override
  public void close() {
    ClosableUtils.close(variantAnnotator);
  }
}
