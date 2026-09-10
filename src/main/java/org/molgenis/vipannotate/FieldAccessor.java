package org.molgenis.vipannotate;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.Record;

@FunctionalInterface
public interface FieldAccessor<F extends Field, R extends Record<F>> {
  F get(R record);
}
