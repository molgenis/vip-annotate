package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.AutoCloseableNoThrow;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class VcfRecordAnnotator<T extends Annotation> implements AutoCloseableNoThrow {
  private final SequenceVariantAnnotator<T> variantAnnotator;
  private final VcfRecordAnnotationWriter<T> annotationWriter;
  private final VcfContigResolver contigRegistry;

  public void annotate(VcfRecord vcfRecord, AnnotationMode annotationMode) {
    Contig contig = contigRegistry.getContig(vcfRecord);
    int start = vcfRecord.getPos().get();
    int stop = start + vcfRecord.getRef().getBaseCount() - 1;

    for (AltAllele altAllele : vcfRecord.getAlt().getAlleles()) {
      SequenceVariant sequenceVariant = createSequenceVariant(contig, start, stop, altAllele);
      T altAnnotation = variantAnnotator.annotate(sequenceVariant);
      annotationWriter.appendAltAnnotation(altAnnotation);
    }

    annotationWriter.writeInfoSubField(vcfRecord, annotationMode);
  }

  public void annotate(List<VcfRecord> vcfRecords, AnnotationMode annotationMode) {
    for (VcfRecord vcfRecord : vcfRecords) {
      annotate(vcfRecord, annotationMode);
    }
  }

  private static SequenceVariant createSequenceVariant(
      Contig contig, int start, int stop, AltAllele altAllele) {
    // perf: reduce allocations and garbage collect pressure
    // @Nullable private SequenceVariant reusableSequenceVariant;
    // TODO perf: reuse SequenceVariant
    return new SequenceVariant(
        contig,
        start,
        stop,
        altAllele,
        SequenceVariantTypeDetector.determineType(stop - start + 1, altAllele));
  }

  @Override
  public void close() {
    ClosableUtils.close(variantAnnotator);
  }
}
