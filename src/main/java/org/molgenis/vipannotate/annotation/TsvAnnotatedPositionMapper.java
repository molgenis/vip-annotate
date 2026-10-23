package org.molgenis.vipannotate.annotation;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.AnnotatedFeatureReaderFactory.TsvColumnsSpec;
import org.molgenis.vipannotate.annotation.def.*;
import org.molgenis.vipannotate.format.tsv.TsvField;
import org.molgenis.vipannotate.format.tsv.TsvRecord;

@RequiredArgsConstructor
public final class TsvAnnotatedPositionMapper
    implements Function<TsvRecord, AnnotatedPosition<CompositeAnnotation>> {
  private final CoordinateSystem coordinateSystem;
  private final TsvColumnsSpec tsvColumnsSpec;
  private final TsvAnnotationMapper annotationMapper;

  @Override
  public AnnotatedPosition<CompositeAnnotation> apply(TsvRecord tsvRecord) {
    Position position = createPosition(tsvRecord);
    CompositeAnnotation annotation = annotationMapper.createAnnotation(tsvRecord);
    return new AnnotatedPosition<>(position, annotation);
  }

  private Position createPosition(TsvRecord tsvRecord) {
    TsvField contigField = tsvRecord.field(tsvColumnsSpec.contig());
    TsvField startField = tsvRecord.field(tsvColumnsSpec.start());

    // FIXME hardcoded length
    // FIXME use contig registry
    Contig contig = new Contig(contigField.toString(), 9);
    int pos = Integer.parseInt(startField.getRawView(), 0, startField.getRawView().length(), 10);
    switch (coordinateSystem) {
      case ZERO_BASED -> pos++;
      case ONE_BASED -> {}
    }
    return new Position(contig, pos);
  }
}
