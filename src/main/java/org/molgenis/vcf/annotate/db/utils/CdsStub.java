package org.molgenis.vcf.annotate.db.utils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.molgenis.vcf.annotate.db.model.ClosedInterval;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CdsStub extends ClosedInterval {
  byte phase;
  @NonNull String proteinId;
}
