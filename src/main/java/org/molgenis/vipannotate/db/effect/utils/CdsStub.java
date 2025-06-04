package org.molgenis.vipannotate.db.effect.utils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.molgenis.vipannotate.db.effect.model.FuryFactory;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CdsStub extends FuryFactory.ClosedInterval {
  byte phase;
  @NonNull String proteinId;
}
