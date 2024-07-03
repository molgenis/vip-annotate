package org.molgenis.vcf.annotate;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vcf.annotate.model.Consequence;

@Value
@Builder
public class TranscriptAnnotation {
  @NonNull Consequence consequence;
  // TODO add @NonNull
  String hgvsC;
  String hgvsP;
  String exon;
  String intron;
}
