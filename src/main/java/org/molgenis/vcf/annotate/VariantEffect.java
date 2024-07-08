package org.molgenis.vcf.annotate;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.molgenis.vcf.annotate.model.Consequence;

import java.util.List;

@Value
@Builder
public class VariantEffect {
  @NonNull @Singular List<Consequence> consequences;
  Integer cdsPosition;
  // FIXME add @NonNull
  String hgvsC;
  String hgvsP;
  String exon;
  String intron;
}
