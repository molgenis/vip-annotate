package org.molgenis.vipannotate.annotation;

public sealed interface ValueDecoder permits U16ToF64ValueDecoder, ByteToStringValueDecoder {}
