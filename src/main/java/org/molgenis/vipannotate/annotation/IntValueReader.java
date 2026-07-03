package org.molgenis.vipannotate.annotation;

public sealed interface IntValueReader extends ValueReader permits U8ValueReader, U16ValueReader {}
