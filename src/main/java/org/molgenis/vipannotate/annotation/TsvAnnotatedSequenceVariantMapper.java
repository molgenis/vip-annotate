package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.AnnotatedFeatureReaderFactory.TsvColumnsSpec;
import org.molgenis.vipannotate.annotation.def.*;
import org.molgenis.vipannotate.format.tsv.TsvField;
import org.molgenis.vipannotate.format.tsv.TsvRecord;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.format.vcf.AltAlleleRegistry;

@RequiredArgsConstructor
public final class TsvAnnotatedSequenceVariantMapper
    implements Function<TsvRecord, AnnotatedSequenceVariant<CompositeAnnotation>> {
  private final CoordinateSystem coordinateSystem;
  private final TsvColumnsSpec tsvColumnsSpec;
  private final TsvAnnotationMapper annotationMapper;

  @Override
  public AnnotatedSequenceVariant<CompositeAnnotation> apply(TsvRecord tsvRecord) {
    SequenceVariant sequenceVariant = createSequenceVariant(tsvRecord);
    CompositeAnnotation annotation = annotationMapper.createAnnotation(tsvRecord);
    return new AnnotatedSequenceVariant<>(sequenceVariant, annotation);
  }

  private SequenceVariant createSequenceVariant(TsvRecord tsvRecord) {
    TsvField contigField = tsvRecord.field(tsvColumnsSpec.contig());
    TsvField startField = tsvRecord.field(tsvColumnsSpec.start());
    TsvField refField = tsvRecord.field(requireNonNull(tsvColumnsSpec.ref()));
    TsvField altField = tsvRecord.field(requireNonNull(tsvColumnsSpec.alt()));

    // FIXME hardcoded length
    // FIXME use contig registry
    Contig contig = new Contig(contigField.toString(), 9);
    int start = Integer.parseInt(startField.getRawView(), 0, startField.getRawView().length(), 10);
    switch (coordinateSystem) {
      case ZERO_BASED -> start++;
      case ONE_BASED -> {}
    }
    int refLen = refField.getRawView().length();
    AltAllele alt = AltAlleleRegistry.INSTANCE.getOrCreate(altField.getRawView());
    return new SequenceVariant(
        contig,
        start,
        start + refLen - 1,
        alt,
        SequenceVariantTypeDetector.determineType(refLen, alt));
  }
}
