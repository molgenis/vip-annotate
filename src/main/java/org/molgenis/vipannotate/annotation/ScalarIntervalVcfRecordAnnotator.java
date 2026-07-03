package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class ScalarIntervalVcfRecordAnnotator<T extends Interval> implements VcfRecordAnnotator {
  private final Predicate<SequenceVariant> canAnnotate;
  private final AnnotationDb<SequenceVariant, ScalarAnnotation> annotationDb;
  private final AnnotationSelector<ScalarAnnotation> annotationSelector;
  private final VcfRecordAnnotationWriter<ScalarAnnotation> annotationWriter;
  private final VcfContigResolver contigRegistry;

  // perf: reduce allocations and garbage collect pressure
  @Nullable private SequenceVariant reusableSequenceVariant;
  @Nullable private List<ScalarAnnotation> reusableAltAnnotations;

  @Override
  public void annotate(VcfRecord vcfRecord, AnnotationMode annotationMode) {
    Contig contig = contigRegistry.getContig(vcfRecord);
    int start = vcfRecord.getPos().get();
    int stop = start + vcfRecord.getRef().getBaseCount() - 1;

    for (AltAllele altAllele : vcfRecord.getAlt().getAlleles()) {
      SequenceVariant sequenceVariant = createSequenceVariant(contig, start, stop, altAllele);
      ScalarAnnotation altAnnotation = findAnnotation(sequenceVariant);
      annotationWriter.appendAltAnnotation(altAnnotation);
    }

    annotationWriter.writeInfoSubField(vcfRecord, annotationMode);
  }

  private static SequenceVariant createSequenceVariant(
      Contig contig, int start, int stop, AltAllele altAllele) {
    // TODO reuse reusableSequenceVariant
    return new SequenceVariant(
        contig,
        start,
        stop,
        altAllele,
        SequenceVariantTypeDetector.determineType(stop - start + 1, altAllele));
  }

  private @Nullable ScalarAnnotation findAnnotation(SequenceVariant sequenceVariant) {
    ScalarAnnotation altAnnotation;
    if (canAnnotate.test(sequenceVariant)) {
      List<ScalarAnnotation> altAnnotations = createAnnotationList();
      annotationDb.findAnnotations(sequenceVariant, altAnnotations);

      altAnnotation = annotationSelector.select(altAnnotations);

    } else {
      altAnnotation = null;
    }
    return altAnnotation;
  }

  private List<ScalarAnnotation> createAnnotationList() {
    if (reusableAltAnnotations == null) {
      reusableAltAnnotations = new ArrayList<>(1);
    } else {
      reusableAltAnnotations.clear();
    }
    return reusableAltAnnotations;
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationDb);
  }
}
