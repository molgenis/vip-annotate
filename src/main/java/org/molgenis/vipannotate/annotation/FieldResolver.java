package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.FieldAccessor;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.Record;

public interface FieldResolver<F extends Field, R extends Record<F>> {
  FieldAccessor<F, R> resolve(String fieldId);
}
