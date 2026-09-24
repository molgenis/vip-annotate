package org.molgenis.vipannotate.annotation.resolved;

public sealed interface IntEncoding
    permits NullableIntEncoding, OffsetIntEncoding, OffsetNullableIntEncoding, PlainIntEncoding {}
