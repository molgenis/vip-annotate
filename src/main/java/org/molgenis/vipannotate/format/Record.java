package org.molgenis.vipannotate.format;

@SuppressWarnings("AvoidCommonTypeNames")
public interface Record<F extends Field> {
  F[] fields();

  F field(int index);
}
