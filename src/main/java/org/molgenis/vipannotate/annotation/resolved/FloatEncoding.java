package org.molgenis.vipannotate.annotation.resolved;

public sealed interface FloatEncoding
    permits NullableFloatEncoding, PlainFloatEncoding, QuantizedEncoding {}
