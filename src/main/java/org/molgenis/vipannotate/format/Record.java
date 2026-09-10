package org.molgenis.vipannotate.format;

public interface Record<F extends Field> {
  F[] fields();

  F field(int index);
}
