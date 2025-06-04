package org.molgenis.vipannotate.annotator.effect;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.molgenis.vipannotate.annotator.effect.model.Consequence;

@Value
@Builder
public class VariantEffect {
  @NonNull @Singular List<Consequence> consequences;
  String hgvsC;
  String hgvsP;
  Integer exonNumber;
  Integer exonTotal;
  Integer intronNumber;
  Integer intronTotal;
}
